package gortea.jgmax.wish_list.screens.add_url.state

import android.graphics.Bitmap
import gortea.jgmax.wish_list.mvi.view.ViewState

data class AddUrlViewState(
    val isLoading: Boolean,
    val loadingProgress: Int,
    val bitmap: Bitmap?,
    val url: String?
) : ViewState {
    companion object {
        val Default = AddUrlViewState(
            isLoading = false,
            loadingProgress = 0,
            bitmap = null,
            url = null
        )
    }
}