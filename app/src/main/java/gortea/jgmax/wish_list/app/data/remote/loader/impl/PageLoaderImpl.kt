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
    private var loadedBitmap: Bitmap? = null
    private var isLoading = false
    private val loaderHandler = Handler(Looper.getMainLooper())

    private fun prepareLoader(withImages: Boolean) {
        loader.prepare(withImages)
    }

    override fun detachLoader() {
        loader.detach()
    }

    private fun saveResult(url: String, bitmap: Bitmap) {
        loadedUrl = url
        loadedBitmap = Bitmap.createBitmap(bitmap)
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
                loadedBitmap?.let { onComplete(Bitmap.createBitmap(it)) }
            } else {
                isLoading = true
                // Workaround to fix narrow page render bug
                val isInitialLoading = uptimeMillis() - loader.getAttachingTime() > INITIAL_TIMEOUT

                loaderHandler.postDelayed({
                    loader.configureBitmapPageLoader({
                        isLoading = false
                        saveResult(url, it)
                        Log.e("bitmap", it.height.toString())
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
