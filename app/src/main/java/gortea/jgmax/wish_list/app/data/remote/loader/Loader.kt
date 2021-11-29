package gortea.jgmax.wish_list.app.data.remote.loader

import android.content.Context
import android.graphics.Bitmap

interface Loader {
    fun attach(context: Context)
    fun getAttachingTime(): Long

    fun configureBitmapPageLoader(
        onComplete: (Bitmap) -> Unit,
        onError: () -> Unit,
        onProgress: (Int) -> Unit = { }
    )

    fun configureHtmlPageLoader(
        onComplete: (String) -> Unit,
        onError: () -> Unit,
        onProgress: (Int) -> Unit = { }
    )

    fun loadUrl(url: String)
    fun prepare(loadImages: Boolean = false)
    fun detach()
    fun clear()
    fun screenshot(): Bitmap?
}