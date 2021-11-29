package gortea.jgmax.wish_list.app.data.remote.loader

import android.graphics.Bitmap

interface PageLoader {
    fun detachLoader()
    fun loadAsHtml(
        url: String,
        onComplete: (String) -> Unit,
        onError: () -> Unit,
        onProgress: (Int) -> Unit = { }
    )

    fun loadAsBitmap(
        url: String,
        onComplete: (Bitmap) -> Unit,
        onError: () -> Unit,
        onProgress: (Int) -> Unit = { }
    )
}