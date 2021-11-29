package gortea.jgmax.wish_list.features.add_url.state

import android.graphics.Bitmap
import gortea.jgmax.wish_list.mvi.domain.State

data class AddUrlState(
    val isLoading: Boolean,
    val isLoadingFailed: Boolean,
    val loadingProgress: Int,
    val pageBitmap: Bitmap?,
    val pageUrl: String?
) : State {
    companion object {
        val Default = AddUrlState(
            isLoading = false,
            isLoadingFailed = false,
            loadingProgress = 0,
            pageBitmap = null,
            pageUrl = null
        )
    }
}