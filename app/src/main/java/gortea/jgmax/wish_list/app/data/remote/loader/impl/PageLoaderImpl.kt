package gortea.jgmax.wish_list.app.data.remote.loader.impl

import android.content.Context
import android.graphics.Bitmap
import gortea.jgmax.wish_list.app.data.remote.loader.Loader
import gortea.jgmax.wish_list.app.data.remote.loader.PageLoader
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

    private fun prepareLoader() {
        loader.prepare(false)
    }

    override fun detach() {
        loadedUrl = ""
        loadingUrl = ""
        if (isBitmapCached) {
            loader.getLoaderContext()?.let {
                removeBitmapCache(PAGE_CACHE_FILE_NAME, it)
                removeBitmapCache(ICON_CACHE_FILE_NAME, it)
            }
        }
        isBitmapCached = false
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

    private fun saveResult(url: String, page: Bitmap, icon: Bitmap?) {
        loadedUrl = url
        loader.getLoaderContext()?.let {
            page.cache(PAGE_CACHE_FILE_NAME, it)
            icon?.cache(ICON_CACHE_FILE_NAME, it)
            isBitmapCached = true
        }
    }

    override fun loadAsBitmap(
        url: String,
        force: Boolean
    ) {
        if (!isLoading || url != loadingUrl || force) {
            if (loadedUrl == url && isBitmapCached && !force) {
                loader.getLoaderContext()?.let { context ->
                    decodeBitmapFromCache(PAGE_CACHE_FILE_NAME, context)?.let { page ->
                        val icon = decodeBitmapFromCache(ICON_CACHE_FILE_NAME, context)
                        onComplete(page, icon)
                    }
                }
            } else {
                loader.getLoaderContext()?.let {
                    removeBitmapCache(PAGE_CACHE_FILE_NAME, it)
                    removeBitmapCache(ICON_CACHE_FILE_NAME, it)
                    isBitmapCached = false
                }

                if (isLoading) {
                    loader.stopLoading()
                }
                isLoading = true
                loadingUrl = url
                loader.configureBitmapPageLoader(
                    onComplete = { result ->
                        isLoading = false
                        saveResult(url, result.page, result.icon)
                        loadingUrl = ""
                        onComplete(result.page, result.icon)
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
                prepareLoader()
                loader.loadUrl(url)
            }
        }
    }

    private companion object {
        private const val PAGE_CACHE_FILE_NAME = "PageLoaderCache"
        private const val ICON_CACHE_FILE_NAME = "IconLoaderCache"
    }
}
