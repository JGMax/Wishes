package gortea.jgmax.wish_list.app.data.remote.loader

import android.content.Context
import gortea.jgmax.wish_list.app.data.remote.loader.data.BitmapLoaderResult

interface Loader {
    fun isAttached(): Boolean
    fun attach(context: Context)

    fun stopLoading()
    fun getAttachingTime(): Long

    fun getLoaderContext(): Context?

    fun configureBitmapPageLoader(
        onComplete: (BitmapLoaderResult) -> Unit,
        onError: () -> Unit,
        onProgress: (Int) -> Unit = { }
    )

    fun loadUrl(url: String)
    fun prepare(loadImages: Boolean = false)
    fun detach()
    fun clear()
}
