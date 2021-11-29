package gortea.jgmax.wish_list.app.data.remote.loader

import android.graphics.Bitmap

interface PageLoader {
    fun detachLoader()

    fun loadAsBitmap(
        url: String,
        onComplete: (Bitmap) -> Unit,
        onError: () -> Unit,
        onProgress: (Int) -> Unit = { },
        withImages: Boolean = false,
        force: Boolean = false
    )
}