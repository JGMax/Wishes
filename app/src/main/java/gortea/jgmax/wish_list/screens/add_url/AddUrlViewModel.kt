package gortea.jgmax.wish_list.screens.add_url

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gortea.jgmax.wish_list.app.data.repository.models.wish.Params
import gortea.jgmax.wish_list.app.data.repository.models.wish.PricePosition
import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import gortea.jgmax.wish_list.features.add_url.action.AddUrlAction
import gortea.jgmax.wish_list.features.add_url.event.AddUrlEvent
import gortea.jgmax.wish_list.features.add_url.state.AddUrlState
import gortea.jgmax.wish_list.features.factory.FeatureFactory
import gortea.jgmax.wish_list.mvi.domain.Feature
import gortea.jgmax.wish_list.mvi.view.AppFragmentViewModel
import gortea.jgmax.wish_list.screens.add_url.action.AddUrlViewAction
import gortea.jgmax.wish_list.screens.add_url.event.AddUrlViewEvent
import gortea.jgmax.wish_list.screens.add_url.state.AddUrlViewState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class AddUrlViewModel @Inject constructor(
    featureFactory: FeatureFactory
) : AppFragmentViewModel<AddUrlViewState, AddUrlViewEvent, AddUrlViewAction, AddUrlState, AddUrlEvent, AddUrlAction>() {
    override val mutableStateFlow = MutableStateFlow(AddUrlViewState.Default)
    override val mutableActionFlow = MutableSharedFlow<AddUrlViewAction?>()

    private val digitsRegex = Regex("[^\\d]+")

    override val feature = featureFactory
        .createFeature<Feature<AddUrlState, AddUrlEvent, AddUrlAction>>(viewModelScope)
        ?: throw IllegalAccessException("Unknown feature")

    init {
        collectFeatureFlows()
    }


    private fun onlyDigits(str: String): String = digitsRegex.replace(str, "")

    override fun bindViewStateToFeatureState(state: AddUrlViewState): AddUrlState {
        return AddUrlState(
            isLoading = state.isLoading,
            isLoadingFailed = false,
            loadingProgress = state.loadingProgress,
            recognitionInProcess = state.recognitionInProcess,
            recognitionResult = state.recognitionResult,
            pageBitmap = state.bitmap,
            pageUrl = state.url
        )
    }

    override fun bindFeatureStateToViewState(state: AddUrlState): AddUrlViewState {
        return mutableStateFlow.value.copy(
            isLoading = state.isLoading,
            loadingProgress = state.loadingProgress,
            recognitionInProcess = state.recognitionInProcess,
            recognitionResult = state.recognitionResult?.let { onlyDigits(it) },
            bitmap = state.pageBitmap,
            url = state.pageUrl
        )
    }

    override fun bindFeatureActionToViewAction(action: AddUrlAction): AddUrlViewAction? {
        return when (action) {
            is AddUrlAction.ShowError -> AddUrlViewAction.ShowError(action.message)
            else -> null
        }
    }

    override fun bindViewEventToFeatureEvent(event: AddUrlViewEvent): AddUrlEvent? {
        return when (event) {
            is AddUrlViewEvent.AddUrl -> AddUrlEvent.AddUrl(event.url)
            is AddUrlViewEvent.RecognizeText -> {
                event.position.run {
                    val selected = Bitmap.createBitmap(
                        event.bitmap,
                        left.toInt(),
                        top.toInt(),
                        (right - left).toInt(),
                        (bottom - top).toInt()
                    )
                    AddUrlEvent.RecognizeText(selected)
                }
            }
            is AddUrlViewEvent.AddWish -> {
                AddUrlEvent.AddWish(
                    WishModel(
                        title = requireNotNull(stateFlow.value.title),
                        currentPrice = requireNotNull(stateFlow.value.recognitionResult).toLong(),
                        params = Params(
                            url = event.url,
                            targetPrice = requireNotNull(stateFlow.value.targetPrice).toLong(),
                            notificationFrequency = stateFlow.value.notificationFrequency.coerceIn(
                                0,
                                48
                            ),
                            pricePosition = requireNotNull(stateFlow.value.selectedPosition).run {
                                PricePosition(
                                    left = left.toInt(),
                                    top = top.toInt(),
                                    right = right.toInt(),
                                    bottom = bottom.toInt()
                                )
                            }
                        )
                    )
                )
            }
            else -> null
        }
    }
}