package gortea.jgmax.wish_list.app.data.remote.loader

import android.content.Context
import android.graphics.Bitmap

interface PageLoader {
    fun detach()
    fun attach(context: Context)

    fun attachListeners(
        onComplete: (Bitmap) -> Unit,
        onError: () -> Unit,
        onProgress: (Int) -> Unit = { }
    )

    fun loadAsBitmap(
        url: String,
        withImages: Boolean = false,
        force: Boolean = false
    )
}