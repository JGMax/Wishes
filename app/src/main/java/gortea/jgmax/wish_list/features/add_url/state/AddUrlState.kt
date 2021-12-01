package gortea.jgmax.wish_list.features.add_url.state

import android.graphics.Bitmap
import gortea.jgmax.wish_list.mvi.domain.State

data class AddUrlState(
    val isLoading: Boolean,
    val isLoadingFailed: Boolean,
    val loadingProgress: Int,
    val recognitionInProcess: Boolean,
    val recognitionResult: String?,
    val pageBitmap: Bitmap?,
    val pageUrl: String?
) : State {
    companion object {
        val Default = AddUrlState(
            isLoading = false,
            isLoadingFailed = false,
            loadingProgress = 0,
            recognitionInProcess = false,
            recognitionResult = null,
            pageBitmap = null,
            pageUrl = null
        )
    }
}