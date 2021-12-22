package gortea.jgmax.wish_list.app.data.remote.loader.impl

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.os.SystemClock.uptimeMillis
import gortea.jgmax.wish_list.app.data.remote.loader.Loader
import gortea.jgmax.wish_list.app.data.remote.loader.PageLoader
import gortea.jgmax.wish_list.app.data.remote.loader.data.BitmapLoaderResult
import gortea.jgmax.wish_list.extentions.cache
import gortea.jgmax.wish_list.extentions.decodeBitmapFromCache
import gortea.jgmax.wish_list.extentions.removeBitmapCache

class PageLoaderImpl(private val loader: Loader) : PageLoader {
    private var loadedUrl = ""
    private var loadingUrl = ""
    private var isBitmapCached = false
    private var isLoading = false

    private var onComplete: (Bitmap, Bitmap?) -> Unit = { _, _ -> }
    private var onError: () -> Unit = {}
    private var onProgress: (Int) -> Unit = {}
    private val loaderHandler = Handler(Looper.getMainLooper())

    private val pageCacheFileName = toString() + "_PAGE_CACHE"
    private val iconCacheFileName = toString() + "_ICON_CACHE"

    private fun prepareLoader() {
        loader.prepare(false)
    }

    override fun detach() {
        removeCache()
        loadedUrl = ""
        loadingUrl = ""
        isLoading = false
        loader.detach()
    }

    override fun attach(context: Context) {
        if (!loader.isAttached()) {
            loader.attach(context)
        }
    }

    override fun attachListeners(
        onComplete: (Bitmap, Bitmap?) -> Unit,
        onError: () -> Unit,
        onProgress: (Int) -> Unit
    ) {
        this.onComplete = onComplete
        this.onError = onError
        this.onProgress = onProgress
    }

    override fun loadAsBitmap(url: String, force: Boolean) {
        if (force || (loadedUrl != url && !isLoading) || (url != loadingUrl && isLoading)) {
            removeCache()
            if (isLoading) {
                loader.stopLoading()
            }
            // Workaround to fix narrow page render bug
            val isInitialLoading = uptimeMillis() - loader.getAttachingTime() > INITIAL_TIMEOUT

            loaderHandler.postDelayed(
                { startLoading(url) },
                if (isInitialLoading) INITIAL_TIMEOUT else 0L
            )
        } else if (!isLoading && isBitmapCached) {
            restoreDataFromCache(onComplete)
        }
    }

    private fun startLoading(url: String) {
        isLoading = true
        loadingUrl = url
        loader.configureBitmapPageLoader(
            onComplete = { result -> onLoadingComplete(url, result) },
            onError = { onLoadingError() },
            onProgress = { onLoadingProgress(it) }
        )
        prepareLoader()
        loader.loadUrl(url)
    }

    private fun onLoadingComplete(url: String, result: BitmapLoaderResult) {
        saveCache(url, result.page, result.icon)
        isLoading = false
        loadingUrl = ""
        onComplete(result.page, result.icon)
    }

    private fun onLoadingError() {
        isLoading = false
        loadingUrl = ""
        onError()
    }

    private fun onLoadingProgress(progress: Int) {
        onProgress(progress)
    }

    private fun saveCache(url: String, page: Bitmap, icon: Bitmap?) {
        loadedUrl = url
        loader.getLoaderContext()?.filesDir?.let {
            page.cache(pageCacheFileName, it)
            icon?.cache(iconCacheFileName, it)
            isBitmapCached = true
        }
    }

    private fun removeCache() {
        if (isBitmapCached) {
            loader.getLoaderContext()?.filesDir?.let {
                removeBitmapCache(pageCacheFileName, it)
                removeBitmapCache(iconCacheFileName, it)
                isBitmapCached = false
            }
        }
    }

    private fun restoreDataFromCache(onComplete: (Bitmap, Bitmap?) -> Unit) {
        loader.getLoaderContext()?.let { context ->
            decodeBitmapFromCache(pageCacheFileName, context.cacheDir)?.let { page ->
                val icon = decodeBitmapFromCache(iconCacheFileName, context.cacheDir)
                onComplete(page, icon)
            }
        }
    }

    private companion object {
        private const val INITIAL_TIMEOUT = 1500L
    }
}
