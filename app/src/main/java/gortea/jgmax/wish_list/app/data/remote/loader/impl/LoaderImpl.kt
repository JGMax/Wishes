package gortea.jgmax.wish_list.app.data.remote.loader.impl

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.os.SystemClock.uptimeMillis
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import gortea.jgmax.wish_list.app.data.remote.loader.Loader
import gortea.jgmax.wish_list.app.data.remote.loader.connection.ConnectionDetector
import gortea.jgmax.wish_list.app.data.remote.loader.data.BitmapLoaderResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoaderImpl(
    context: Context? = null
) : Loader {

    private var instance: WebView? = null
    private var bitmapIcon: Bitmap? = null
    private var isHandled = false
    private var isCompleted = false
    private var isLoading = false
    private val resultHandler = Handler(Looper.getMainLooper())
    private var handlerJob: Job? = null
    private var loadingStarted = 0L
    private var loadingProgress = -1
    private var timeoutError = false
    private var onError: () -> Unit = { }
    private var onComplete: (BitmapLoaderResult) -> Unit = { }
    private var onProgress: (Int) -> Unit = { }
    private var attachingTime = 0L

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
        configureBitmapPageLoader({ attachingTime = uptimeMillis() }, {})
        prepare()
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
        isLoading = false
        timeoutError = false
        instance?.stopLoading()
    }

    override fun getAttachingTime(): Long = attachingTime

    override fun getLoaderContext(): Context? = instance?.context

    override fun configureBitmapPageLoader(
        onComplete: (BitmapLoaderResult) -> Unit,
        onError: () -> Unit,
        onProgress: (Int) -> Unit
    ) {
        this.onError = onError
        this.onComplete = onComplete
        this.onProgress = onProgress

        handlerJob?.cancel()
        instance?.apply {
            bitmapIcon = null
            webViewClient = DefaultWebViewClient(
                onComplete = { onLoadingComplete() },
                onError = { onLoadingError() },
                onProgress = { onLoadingProgress(it) },
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

    private fun onLoadingProgress(progress: Int) {
        if (loadingProgress != progress) {
            loadingProgress = progress
            loadingStarted = uptimeMillis()
        }
        onProgress(progress)
    }

    private fun onLoadingError() {
        isLoading = false
        onError()
    }

    private fun onLoadingComplete() {
        isCompleted = true
        handleRedirection {
            resultHandler.post {
                isLoading = false
                instance?.screenshot()?.let { onComplete(BitmapLoaderResult(bitmapIcon, it)) }
                    ?: onError()
            }
        }
    }

    private fun handleRedirection(onRedirectingFinished: () -> Unit) {
        if (!isHandled) {
            handlerJob = CoroutineScope(Dispatchers.Default).launch {
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
        isLoading = false
        timeoutError = false
        instance?.loadUrl(url) ?: throw IllegalStateException("Loader is detached")

        loadingStarted = uptimeMillis()
        isLoading = true
        handleLoadingTimeout {
            isLoading = false
            timeoutError = true
        }
    }


    private fun handleLoadingTimeout(onTimeout: () -> Unit) {
        CoroutineScope(Dispatchers.Unconfined).launch {
            while (isLoading && uptimeMillis() - loadingStarted < LOADING_PROGRESS_CHANGE_TIMEOUT) {
                delay(LOADING_PROGRESS_CHANGE_CHECK_INTERVAL)
            }
            if (uptimeMillis() - loadingStarted >= LOADING_PROGRESS_CHANGE_TIMEOUT) {
                onTimeout()
            }
        }
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

    private open inner class DefaultWebViewClient(
        private val onComplete: (view: WebView) -> Unit,
        private val onError: () -> Unit,
        private val onProgress: (Int) -> Unit,
        context: Context?
    ) : WebViewClient() {
        private var hasConnection = true
        private var detectorJob: Job? = null
        private val connectionDetector = context?.let { ConnectionDetector(it) }

        private var isLoadingHandled = false
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
            if (hasConnection && !timeoutError) {
                if (detectorJob == null) {
                    detectConnection()
                }

                if (progress == 100) {
                    stopDetection()
                    onComplete(view)
                    isLoadingHandled = false
                } else {
                    handler.postDelayed({ handleLoadingProcess(view) }, PROGRESS_CHECK_INTERVAL)
                }
            } else {
                view.stopLoading()
                stopDetection()
                onError()
                isLoadingHandled = false
            }
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            if (!isLoadingHandled) {
                isLoadingHandled = true
                view?.let { handleLoadingProcess(view) }
            }
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            if (!isLoadingHandled) {
                isLoadingHandled = true
                view?.let { handleLoadingProcess(view) }
            }
            super.onPageFinished(view, url)
        }
    }

    private companion object {
        private const val PROGRESS_CHECK_INTERVAL = 100L
        private const val REDIRECTION_DELAY = 3500L
        private const val LOADING_PROGRESS_CHANGE_TIMEOUT = 15000L
        private const val LOADING_PROGRESS_CHANGE_CHECK_INTERVAL = 1000L
    }
}
