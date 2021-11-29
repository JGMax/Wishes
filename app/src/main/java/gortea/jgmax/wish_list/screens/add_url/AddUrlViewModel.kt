package gortea.jgmax.wish_list.screens.add_url

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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

    override val feature = featureFactory
        .createFeature<Feature<AddUrlState, AddUrlEvent, AddUrlAction>>(viewModelScope)
        ?: throw IllegalAccessException("Unknown feature")

    init {
        collectFeatureAction()
        collectFeatureState()
    }

    override fun bindViewStateToFeatureState(state: AddUrlViewState): AddUrlState {
        return AddUrlState(
            isLoading = state.isLoading,
            isLoadingFailed = false,
            loadingProgress = state.loadingProgress,
            pageBitmap = state.bitmap,
            pageUrl = state.url
        )
    }

    override fun bindFeatureStateToViewState(state: AddUrlState): AddUrlViewState {
        return AddUrlViewState(
            isLoading = state.isLoading,
            loadingProgress = state.loadingProgress,
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
            else -> null
        }
    }
}