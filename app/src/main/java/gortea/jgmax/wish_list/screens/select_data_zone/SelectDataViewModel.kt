package gortea.jgmax.wish_list.screens.select_data_zone

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gortea.jgmax.wish_list.R
import gortea.jgmax.wish_list.app.data.repository.models.wish.Position
import gortea.jgmax.wish_list.features.factory.FeatureFactory
import gortea.jgmax.wish_list.features.select_data_zone.action.SelectDataZoneAction
import gortea.jgmax.wish_list.features.select_data_zone.event.SelectDataZoneEvent
import gortea.jgmax.wish_list.features.select_data_zone.state.SelectDataZoneState
import gortea.jgmax.wish_list.mvi.view.AppFragmentViewModel
import gortea.jgmax.wish_list.navigation.coordinator.Coordinator
import gortea.jgmax.wish_list.screens.select_data_zone.action.SelectDataViewAction
import gortea.jgmax.wish_list.screens.select_data_zone.data.Result
import gortea.jgmax.wish_list.screens.select_data_zone.event.SelectDataViewEvent
import gortea.jgmax.wish_list.screens.select_data_zone.state.SelectDataViewState
import gortea.jgmax.wish_list.screens.select_data_zone.view.BitmapStorage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SelectDataViewModel @Inject constructor(
    featureFactory: FeatureFactory,
    private val coordinator: Coordinator
) : AppFragmentViewModel<SelectDataViewState, SelectDataViewEvent, SelectDataViewAction, SelectDataZoneState, SelectDataZoneEvent, SelectDataZoneAction>() {
    override val mutableStateFlow = MutableStateFlow(SelectDataViewState.Default)
    override val mutableActionFlow = MutableSharedFlow<SelectDataViewAction?>()

    private val digitsRegex = Regex("[^\\d]+")

    val bitmapStorage = object : BitmapStorage {
        override var bitmaps: MutableList<Bitmap?> = mutableListOf(null, null)
    }

    override val feature = featureFactory
        .createFeature<SelectDataZoneState, SelectDataZoneEvent, SelectDataZoneAction>(
            viewModelScope
        )
        ?: throw IllegalAccessException("Unknown feature")

    init {
        collectFeatureFlows()
    }

    private fun onlyDigits(str: String): String = digitsRegex.replace(str, "")

    override fun bindViewStateToFeatureState(state: SelectDataViewState): SelectDataZoneState {
        return SelectDataZoneState(
            isLoading = state.isLoading,
            isLoadingFailed = state.isLoadingFailed,
            loadingProgress = state.loadingProgress,
            recognitionInProcess = state.recognitionInProcess,
            recognitionResult = state.recognitionResult,
            pageBitmap = state.bitmap,
            pageUrl = state.url
        )
    }

    override fun bindFeatureStateToViewState(state: SelectDataZoneState): SelectDataViewState {
        return mutableStateFlow.value.copy(
            isLoading = state.isLoading,
            isLoadingFailed = state.isLoadingFailed,
            loadingProgress = state.loadingProgress,
            recognitionInProcess = state.recognitionInProcess,
            recognitionResult = state.recognitionResult?.let { onlyDigits(it) },
            bitmap = state.pageBitmap,
            url = state.pageUrl
        )
    }

    override fun bindFeatureActionToViewAction(action: SelectDataZoneAction): SelectDataViewAction? {
        return when (action) {
            is SelectDataZoneAction.LoadingFailed -> {
                SelectDataViewAction.ShowMessage(R.string.loading_failed)
            }
            is SelectDataZoneAction.ReturnValue -> {
                coordinator.returnResult(
                    Result(action.value, action.position),
                    Coordinator.DETECTION_RESULT
                )
                coordinator.navigateBack()
                null
            }
            is SelectDataZoneAction.SelectionIsNotFull -> {
                SelectDataViewAction.ShowMessage(R.string.data_is_not_recognized_error)
            }
            else -> null
        }
    }

    override fun bindViewEventToFeatureEvent(event: SelectDataViewEvent): SelectDataZoneEvent? {
        return when (event) {
            is SelectDataViewEvent.LoadUrl -> SelectDataZoneEvent.LoadUrl(stateFlow.value.url)
            is SelectDataViewEvent.RecognizeText -> {
                event.position.run {
                    val selected = Bitmap.createBitmap(
                        event.bitmap,
                        left.toInt(),
                        top.toInt(),
                        (right - left).toInt(),
                        (bottom - top).toInt()
                    )
                    SelectDataZoneEvent.RecognizeText(selected)
                }
            }
            is SelectDataViewEvent.UrlIsNull -> {
                sendViewAction(SelectDataViewAction.ShowMessage(R.string.incorrect_url))
                coordinator.navigateBack()
                null
            }
            is SelectDataViewEvent.OnFABClick -> {
                stateFlow.value.run {
                    if (isSelectionActive && !recognitionInProcess && !recognitionResult.isNullOrEmpty()) {
                        SelectDataZoneEvent.AcceptSelection(
                            recognitionResult,
                            selectedPosition?.run {
                                Position(
                                    left = left.toInt(),
                                    top = top.toInt(),
                                    right = right.toInt(),
                                    bottom = bottom.toInt()
                                )
                            }
                        )
                    } else {
                        if (isSelectionActive) {
                            sendViewAction(SelectDataViewAction.ShowMessage(R.string.data_is_not_recognized_error))
                        } else {
                            handleState(stateFlow.value.copy(isSelectionActive = true))
                        }
                        null
                    }
                }
            }
            is SelectDataViewEvent.OnBackPressed -> {
                stateFlow.value.run {
                    if (isSelectionActive) {
                        handleState(stateFlow.value.copy(isSelectionActive = false))
                    } else {
                        coordinator.navigateBack()
                    }
                }
                null
            }
        }
    }

    companion object {
        const val URL_ARG = "URL_ARG"
        const val IS_LOADING_ARG = "IS_LOADING_ARG"
        const val LOADING_PROGRESS_ARG = "LOADING_PROGRESS_ARG"
    }
}