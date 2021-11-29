package gortea.jgmax.wish_list.app.data.remote.loader.impl

import android.graphics.Bitmap
import gortea.jgmax.wish_list.app.data.remote.loader.Loader
import gortea.jgmax.wish_list.app.data.remote.loader.PageLoader


class PageLoaderImpl(private val loader: Loader) : PageLoader {
    private var loadedUrl = ""
    private var loadedHtml = ""
    private var loadedBitmap: Bitmap? = null
    private var isLoading = false

    private fun prepareLoader() {
        loader.prepare()
    }

    override fun detachLoader() {
        loader.detach()
    }

    override fun loadAsHtml(
        url: String,
        onComplete: (String) -> Unit,
        onError: () -> Unit,
        onProgress: (Int) -> Unit
    ) {
        if(!isLoading) {
            if (loadedUrl == url) {
                onProgress(100)
                onComplete(loadedHtml)
            } else {
                isLoading = true
                prepareLoader()
                loader.configureHtmlPageLoader({
                    isLoading = false
                    loadedUrl = url
                    loadedHtml = it
                    onComplete(it)
                }, {
                    isLoading = false
                    onError()
                }, onProgress)
                loader.loadUrl(url)
            }
        }
    }

    override fun loadAsBitmap(
        url: String,
        onComplete: (Bitmap) -> Unit,
        onError: () -> Unit,
        onProgress: (Int) -> Unit
    ) {
        if (!isLoading) {
            if (loadedUrl == url && loadedBitmap != null) {
                onProgress(100)
                loadedBitmap?.let { onComplete(it) }
            } else {
                isLoading = true
                prepareLoader()
                loader.configureBitmapPageLoader({
                    isLoading = false
                    loadedUrl = url
                    loadedBitmap = it
                    onComplete(it)
                }, {
                    isLoading = false
                    onError()
                }, onProgress)
                loader.loadUrl(url)
            }
        }
    }
}
