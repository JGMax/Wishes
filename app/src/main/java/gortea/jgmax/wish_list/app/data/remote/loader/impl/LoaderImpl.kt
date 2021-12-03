package gortea.jgmax.wish_list.app.data.remote.loader.impl

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.os.SystemClock.uptimeMillis
import android.util.Patterns
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import gortea.jgmax.wish_list.app.data.remote.loader.Loader
import gortea.jgmax.wish_list.app.data.remote.loader.connection.ConnectionDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class LoaderImpl(
    context: Context? = null
) : Loader {

    private var instance: WebView? = null
    private var context: Context? = null
    private var attachingTime = 0L

    init {
        context?.let { attach(it) }
    }

    override fun isAttached(): Boolean = instance != null

    override fun attach(context: Context) {
        detach()
        WebView.enableSlowWholeDocumentDraw()

        this.context = context
        instance = WebView(context)
        setViewPortWidth()

        // Workaround to fix narrow page render bug

        configureBitmapPageLoader({ attachingTime = uptimeMillis() }, {})
        prepare()
        loadUrl("about:blank")
    }

    override fun getAttachingTime(): Long = attachingTime
    override fun stopLoading() {
        instance?.stopLoading()
    }

    private fun setViewPortWidth() {
        context?.apply {
            val width = resources.displayMetrics.run { widthPixels.coerceAtMost(heightPixels) }
            instance?.apply {
                measure(
                    View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY)
                )
                layout(0, 0, measuredWidth, measuredHeight)
            }
        }
    }

    override fun configureBitmapPageLoader(
        onComplete: (Bitmap) -> Unit,
        onError: () -> Unit,
        onProgress: (Int) -> Unit
    ) {
        instance?.apply {
            webViewClient = DefaultWebViewClient(
                onComplete = {
                    it.screenshot()?.let { bitmap -> onComplete(bitmap) } ?: onError()
                },
                onError = onError,
                onProgress = onProgress,
                context = context
            )
        }
    }

    override fun configureHtmlPageLoader(
        onComplete: (String) -> Unit,
        onError: () -> Unit,
        onProgress: (Int) -> Unit
    ) {
        instance?.run {
            removeJavascriptInterface(HtmlInterceptorWebViewClient.JSHtmlInterceptor.name)
            addJavascriptInterface(
                HtmlInterceptorWebViewClient.JSHtmlInterceptor(onComplete),
                HtmlInterceptorWebViewClient.JSHtmlInterceptor.name
            )
            webViewClient = HtmlInterceptorWebViewClient(
                onComplete = {},
                onError = onError,
                onProgress = onProgress,
                context = context
            )
        }
    }

    override fun loadUrl(url: String) {
        instance?.loadUrl(url) ?: throw IllegalStateException("Loader is detached")
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun prepare(loadImages: Boolean) {
        setViewPortWidth()
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
    }

    override fun detach() {
        clear()
        context = null
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

    override fun screenshot(): Bitmap? {
        return instance?.screenshot()
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
                    handler.postDelayed({ onComplete(view) }, RENDER_DELAY)
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

    private class HtmlInterceptorWebViewClient(
        onComplete: (WebView) -> Unit,
        onError: () -> Unit,
        onProgress: (Int) -> Unit,
        context: Context?
    ) : DefaultWebViewClient(
        onComplete = { view ->
            val javascript =
                "javascript:window.JSBridge.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');"
            view.evaluateJavascript(javascript, null)
            onComplete(view)
        },
        onError = onError,
        onProgress = onProgress,
        context = context
    ) {
        class JSHtmlInterceptor(val onComplete: (String) -> Unit) {
            @JavascriptInterface
            fun showHTML(html: String) {
                onComplete(html)
            }

            companion object {
                const val name = "JSHtmlInterceptor"
            }
        }
    }

    private companion object {
        private const val RENDER_DELAY = 1500L
    }
}