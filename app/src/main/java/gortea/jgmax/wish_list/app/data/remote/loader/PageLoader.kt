package gortea.jgmax.wish_list.app.data.remote.loader

import android.graphics.Bitmap

interface PageLoader {
    fun detachLoader()

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