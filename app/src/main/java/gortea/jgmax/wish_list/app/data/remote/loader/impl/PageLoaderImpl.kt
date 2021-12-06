package gortea.jgmax.wish_list.app.data.remote.loader.impl

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.os.SystemClock.uptimeMillis
import gortea.jgmax.wish_list.app.data.remote.loader.Loader
import gortea.jgmax.wish_list.app.data.remote.loader.PageLoader
import gortea.jgmax.wish_list.extentions.cache
import gortea.jgmax.wish_list.extentions.decodeBitmapFromCache
import gortea.jgmax.wish_list.extentions.removeBitmapCache


class PageLoaderImpl(private val loader: Loader) : PageLoader {
    private var loadedUrl = ""
    private var loadingUrl = ""
    private var loadedWithImages: Boolean? = null
    private var isBitmapCached = false
    private var isLoading = false
    private val loaderHandler = Handler(Looper.getMainLooper())

    private var onComplete: (Bitmap) -> Unit = {}
    private var onError: () -> Unit = {}
    private var onProgress: (Int) -> Unit = {}

    private fun prepareLoader(withImages: Boolean) {
        loader.prepare(withImages)
    }

    override fun detach() {
        loadedUrl = ""
        loadingUrl = ""
        loadedWithImages = null
        if (isBitmapCached) {
            loader.getLoaderContext()?.let { removeBitmapCache(CACHE_FILE_NAME, it) }
        }
        isBitmapCached = false
        isLoading = false
        loader.detach()
    }

    override fun attach(context: Context) {
        if(!loader.isAttached()) {
            loader.attach(context)
        }
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
        loader.getLoaderContext()?.let {
            bitmap.cache(CACHE_FILE_NAME, it)
            isBitmapCached = true
        }
        loadedWithImages = withImages
    }

    override fun loadAsBitmap(
        url: String,
        withImages: Boolean,
        force: Boolean
    ) {
        if (!isLoading || url != loadingUrl || force) {
            if (loadedUrl == url && loadedWithImages == withImages && isBitmapCached && !force) {
                loader.getLoaderContext()?.let { context ->
                    decodeBitmapFromCache(CACHE_FILE_NAME, context)?.let {
                        onComplete(it)
                    }
                }
            } else {
                loader.getLoaderContext()?.let {
                    removeBitmapCache(CACHE_FILE_NAME, it)
                    isBitmapCached = false
                }
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
        private const val CACHE_FILE_NAME = "BitmapLoaderCache"
    }
}
