package gortea.jgmax.wish_list.app.data.remote.loader.impl

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.os.SystemClock.uptimeMillis
import gortea.jgmax.wish_list.app.data.remote.loader.Loader
import gortea.jgmax.wish_list.app.data.remote.loader.PageLoader


class PageLoaderImpl(private val loader: Loader) : PageLoader {
    private var loadedUrl = ""
    private var loadedBitmap: Bitmap? = null
    private var isLoading = false
    private var isInitialLoading = true
    private val loaderHandler = Handler(Looper.getMainLooper())

    private fun prepareLoader(withImages: Boolean) {
        loader.prepare(withImages)
    }

    override fun detachLoader() {
        loader.detach()
    }

    private fun saveResult(url: String, bitmap: Bitmap) {
        loadedUrl = url
        loadedBitmap = bitmap
    }

    override fun loadAsBitmap(
        url: String,
        onComplete: (Bitmap) -> Unit,
        onError: () -> Unit,
        onProgress: (Int) -> Unit,
        withImages: Boolean,
        force: Boolean
    ) {
        if (!isLoading) {
            if (loadedUrl == url && loadedBitmap != null && !force) {
                onProgress(100)
                loadedBitmap?.let { onComplete(it) }
            } else {
                isLoading = true
                // Workaround to fix narrow page render bug
                isInitialLoading = uptimeMillis() - loader.getAttachingTime() > INITIAL_TIMEOUT

                loaderHandler.postDelayed({
                    loader.configureBitmapPageLoader({
                        isLoading = false
                        saveResult(url, it)
                        onComplete(it)
                    }, {
                        isLoading = false
                        onError()
                    }, onProgress)
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
