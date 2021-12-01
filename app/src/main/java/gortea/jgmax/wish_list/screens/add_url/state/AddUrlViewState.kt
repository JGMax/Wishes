package gortea.jgmax.wish_list.screens.add_url.state

import android.graphics.Bitmap
import gortea.jgmax.wish_list.mvi.view.ViewState
import gortea.jgmax.wish_list.screens.add_url.view.SelectableImageView

data class AddUrlViewState(
    val isLoading: Boolean,
    val loadingProgress: Int,
    val recognitionInProcess: Boolean,
    val recognitionResult: String?,
    val bitmap: Bitmap?,
    val url: String?,
    val isSelectInProcess: Boolean,
    val selectedPosition: SelectableImageView.SelectedPosition?,
    val title: String?,
    val targetPrice: String?,
    val notificationFrequency: Int
) : ViewState {
    companion object {
        val Default = AddUrlViewState(
            isLoading = false,
            loadingProgress = 0,
            recognitionInProcess = false,
            recognitionResult = null,
            bitmap = null,
            url = null,
            isSelectInProcess = false,
            selectedPosition = null,
            title = null,
            targetPrice = null,
            notificationFrequency = 0
        )
    }
}
