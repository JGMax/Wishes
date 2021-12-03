package gortea.jgmax.wish_list.screens.select_data_zone.state

import android.graphics.Bitmap
import gortea.jgmax.wish_list.R
import gortea.jgmax.wish_list.mvi.view.ViewState
import gortea.jgmax.wish_list.screens.select_data_zone.view.SelectableImageView

data class SelectDataViewState(
    val isLoading: Boolean,
    val loadingProgress: Int,
    val isLoadingFailed: Boolean,
    val recognitionInProcess: Boolean,
    val recognitionResult: String?,
    val bitmap: Bitmap?,
    val url: String,
    val isSelectionActive: Boolean,
    val selectedPosition: SelectableImageView.SelectedPosition?
) : ViewState {
    val fabResource: Int
        get() = if (isSelectionActive) R.drawable.ic_check else R.drawable.ic_edit
    val isFabEnabled: Boolean
        get() = !isLoading
    val recognitionResultText: String
        get() = if (recognitionResult.isNullOrEmpty()) "???" else recognitionResult

    companion object {
        val Default = SelectDataViewState(
            isLoading = true,
            loadingProgress = 0,
            isLoadingFailed = false,
            recognitionInProcess = false,
            recognitionResult = null,
            bitmap = null,
            url = "",
            isSelectionActive = false,
            selectedPosition = null
        )
    }
}
