package gortea.jgmax.wish_list.app.data.remote.loader.impl

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import gortea.jgmax.wish_list.app.data.remote.loader.Loader
import gortea.jgmax.wish_list.app.data.remote.loader.connection.ConnectionDetector
import gortea.jgmax.wish_list.app.data.remote.loader.data.BitmapLoaderResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect


class LoaderImpl(
    context: Context? = null
) : Loader {

    private var instance: WebView? = null
    private var bitmapIcon: Bitmap? = null
    private var isHandled = false
    private var isCompleted = false
    private val resultHandler = Handler(Looper.getMainLooper())
    private var handlerJob: Job? = null

    init {
        context?.let { attach(it) }
    }

    override fun isAttached(): Boolean = instance != null

    override fun attach(context: Context) {
        detach()
        WebView.enableSlowWholeDocumentDraw()

        instance = WebView(context)
        setWidth()
        // Workaround to fix tiny pages bug
        loadUrl("about:blank")
    }

    private fun setWidth() {
        instance?.apply {
            minimumWidth = resources.displayMetrics.run { widthPixels.coerceAtMost(heightPixels) }
            measure(
                View.MeasureSpec.makeMeasureSpec(minimumWidth, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            layout(0, 0, measuredWidth, measuredHeight)
        }
    }

    override fun stopLoading() {
        instance?.stopLoading()
    }

    override fun getLoaderContext(): Context? = instance?.context

    override fun configureBitmapPageLoader(
        onComplete: (BitmapLoaderResult) -> Unit,
        onError: () -> Unit,
        onProgress: (Int) -> Unit
    ) {
        handlerJob?.cancel()
        instance?.apply {
            bitmapIcon = null
            webViewClient = DefaultWebViewClient(
                onComplete = { view ->
                    isCompleted = true
                    handleRedirection(
                        onRedirectingFinished = {
                            resultHandler.post {
                                view.screenshot()?.let {
                                    onComplete(BitmapLoaderResult(bitmapIcon, it))
                                } ?: onError()
                            }
                        }
                    )
                },
                onError = onError,
                onProgress = onProgress,
                context = context
            )
            webChromeClient = object : WebChromeClient() {
                override fun onReceivedIcon(view: WebView, icon: Bitmap) {
                    super.onReceivedIcon(view, icon)
                    bitmapIcon = icon
                }
            }
        }
    }

    private fun handleRedirection(
        onRedirectingFinished: () -> Unit
    ) {
        if (!isHandled) {
            handlerJob = CoroutineScope(Dispatchers.Default)
                .launch {
                    try {
                        isHandled = true
                        while (isCompleted) {
                            isCompleted = false
                            delay(REDIRECTION_DELAY)
                        }
                        isHandled = false
                        onRedirectingFinished()
                    } catch (e: CancellationException) {
                        isHandled = false
                    }
                }
        }
    }

    override fun loadUrl(url: String) {
        instance?.loadUrl(url) ?: throw IllegalStateException("Loader is detached")
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun prepare(loadImages: Boolean) {
        instance?.setLayerType(View.LAYER_TYPE_NONE, null)
        instance?.resumeTimers()
        instance?.visibility = View.INVISIBLE
        instance?.settings?.apply {
            javaScriptEnabled = true
            blockNetworkImage = !loadImages
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            loadsImagesAutomatically = loadImages
            setGeolocationEnabled(false)
            setSupportZoom(false)
        }
        setWidth()
    }

    override fun detach() {
        clear()
        instance = null
    }

    override fun clear() {
        instance?.apply {
            clearHistory()
            clearCache(true)
            loadUrl("about:blank")
            onPause()
            removeAllViews()
            pauseTimers()
            destroy()
        }
    }

    private fun WebView.screenshot(): Bitmap? {
        measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        layout(0, 0, measuredWidth, measuredHeight)
        if (measuredWidth <= 0 || measuredHeight <= 0) {
            return null
        }
        return try {
            val bitmap = Bitmap.createBitmap(
                measuredWidth,
                measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            val paint = Paint()
            val iHeight = bitmap.height.toFloat()
            canvas.drawBitmap(bitmap, 0f, iHeight, paint)
            draw(canvas)
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    private open class DefaultWebViewClient(
        private val onComplete: (view: WebView) -> Unit,
        private val onError: () -> Unit,
        private val onProgress: (Int) -> Unit,
        context: Context?
    ) : WebViewClient() {
        private var hasError = false
        private var hasConnection = true
        private var detectorJob: Job? = null
        private val connectionDetector = context?.let { ConnectionDetector(it) }

        private val handler = Handler(Looper.getMainLooper())

        private fun detectConnection() {
            connectionDetector?.detect()
            detectorJob = CoroutineScope(Dispatchers.Default).launch {
                connectionDetector?.isConnected?.collect { hasConnection = it }
            }
        }

        private fun stopDetection() {
            detectorJob?.cancel()
            detectorJob = null
            connectionDetector?.stopDetection()
        }

        private fun handleLoadingProcess(view: WebView) {
            val progress = view.progress
            onProgress(progress)
            if (!hasError && hasConnection) {
                if (detectorJob == null) {
                    detectConnection()
                }

                if (progress == 100) {
                    stopDetection()
                    onComplete(view)
                } else {
                    handler.postDelayed({ handleLoadingProcess(view) }, PROGRESS_CHECK_INTERVAL)
                }
            } else {
                view.stopLoading()
                stopDetection()
                onError()
            }
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            view?.let { handleLoadingProcess(view) }
            hasError = !Patterns.WEB_URL.matcher(url.toString()).matches()
            super.onPageStarted(view, url, favicon)
        }

        override fun onReceivedError(
            view: WebView?,
            errorCode: Int,
            description: String?,
            failingUrl: String?
        ) {
            if (errorCode == -2) {
                hasError = true
                view?.let { handleLoadingProcess(view) }
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            hasError = !Patterns.WEB_URL.matcher(url.toString()).matches()
            view?.let { handleLoadingProcess(view) }
            super.onPageFinished(view, url)
        }

        private companion object {
            private const val PROGRESS_CHECK_INTERVAL = 100L
        }
    }

    private companion object {
        private const val REDIRECTION_DELAY = 3500L
    }
}
