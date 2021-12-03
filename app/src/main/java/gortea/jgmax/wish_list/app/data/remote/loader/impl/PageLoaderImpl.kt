package gortea.jgmax.wish_list.app.data.remote.loader.impl

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.os.SystemClock.uptimeMillis
import android.util.Log
import gortea.jgmax.wish_list.app.data.remote.loader.Loader
import gortea.jgmax.wish_list.app.data.remote.loader.PageLoader


class PageLoaderImpl(private val loader: Loader) : PageLoader {
    private var loadedUrl = ""
    private var loadingUrl = ""
    private var loadedBitmap: Bitmap? = null
    private var loadedWithImages: Boolean? = null
    private var isLoading = false
    private val loaderHandler = Handler(Looper.getMainLooper())

    private var onComplete: (Bitmap) -> Unit = {}
    private var onError: () -> Unit = {}
    private var onProgress: (Int) -> Unit = {}

    private fun prepareLoader(withImages: Boolean) {
        loader.prepare(withImages)
    }

    override fun detachLoader() {
        loader.detach()
    }

    override fun attachListeners(
        onComplete: (Bitmap) -> Unit,
        onError: () -> Unit,
        onProgress: (Int) -> Unit
    ) {
        this.onComplete = onComplete
        this.onError = onError
        this.onProgress = onProgress
    }

    private fun saveResult(url: String, bitmap: Bitmap, withImages: Boolean) {
        loadedUrl = url
        loadedBitmap = Bitmap.createBitmap(bitmap)
        loadedWithImages = withImages
    }

    override fun loadAsBitmap(
        url: String,
        withImages: Boolean,
        force: Boolean
    ) {
        if (!isLoading || url != loadingUrl || force) {
            if (loadedUrl == url && loadedWithImages == withImages && loadedBitmap != null && !force) {
                loadedBitmap?.let { onComplete(Bitmap.createBitmap(it)) }
            } else {
                // Workaround to fix narrow page render bug
                val isInitialLoading = uptimeMillis() - loader.getAttachingTime() > INITIAL_TIMEOUT

                if (isLoading) {
                    loader.stopLoading()
                }
                isLoading = true
                loadingUrl = url
                loaderHandler.postDelayed({
                    loader.configureBitmapPageLoader(
                        onComplete = {
                            isLoading = false
                            saveResult(url, it, withImages)
                            loadingUrl = ""
                            onComplete(it)
                        },
                        onError = {
                            isLoading = false
                            loadingUrl = ""
                            onError()
                        },
                        onProgress = {
                            onProgress(it)
                        }
                    )
                    prepareLoader(withImages)
                    loader.loadUrl(url)
                }, if (isInitialLoading) INITIAL_TIMEOUT else 0L)
            }
        }
    }

    private companion object {
        private const val INITIAL_TIMEOUT = 1500L
    }
}
