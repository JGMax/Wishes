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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
            val isInitialLoading = uptimeMillis() - loader.getAttachingTime() < INITIAL_TIMEOUT
            val timeout = if (isInitialLoading) INITIAL_TIMEOUT else 0L

            loaderHandler.postDelayed({ startLoading(url) }, timeout)
        } else if (!isLoading) {
            restoreDataFromCache(onComplete) {
                isBitmapCached = false
                loadAsBitmap(url, true)
            }
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
        saveCache(result.page, result.icon)
        loadedUrl = url
        loadingUrl = ""
        onComplete(result.page, result.icon)
        isLoading = false
    }

    private fun onLoadingError() {
        isLoading = false
        loadingUrl = ""
        onError()
    }

    private fun onLoadingProgress(progress: Int) {
        onProgress(progress)
    }

    private fun saveCache(page: Bitmap, icon: Bitmap?) {
        loader.getLoaderContext()?.cacheDir?.let { cacheDir ->
            CoroutineScope(Dispatchers.IO).launch {
                val pageJob = launch {
                    page.cache(pageCacheFileName, cacheDir)
                }
                launch {
                    icon?.cache(iconCacheFileName, cacheDir)
                }.join()
                pageJob.join()
                isBitmapCached = true
            }
        }
    }

    private fun removeCache() {
        if (isBitmapCached) {
            loader.getLoaderContext()?.cacheDir?.let { cacheDir ->
                CoroutineScope(Dispatchers.IO).launch {
                    removeBitmapCache(pageCacheFileName, cacheDir)
                    removeBitmapCache(iconCacheFileName, cacheDir)
                    isBitmapCached = false
                }
            }
        }
    }

    private fun restoreDataFromCache(onComplete: (Bitmap, Bitmap?) -> Unit, onError: () -> Unit) {
        loader.getLoaderContext()?.cacheDir?.let { cacheDir ->
            CoroutineScope(Dispatchers.IO).launch {
                while(!isBitmapCached) {
                    delay(2)
                }
                decodeBitmapFromCache(pageCacheFileName, cacheDir)?.let { page ->
                    val icon = decodeBitmapFromCache(iconCacheFileName, cacheDir)
                    onComplete(page, icon)
                } ?: onError()
            }
        } ?: onError()
    }

    private companion object {
        private const val INITIAL_TIMEOUT = 1500L
    }
}
