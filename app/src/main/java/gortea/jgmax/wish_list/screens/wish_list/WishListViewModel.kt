package gortea.jgmax.wish_list.screens.wish_list

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gortea.jgmax.wish_list.features.factory.FeatureFactory
import gortea.jgmax.wish_list.features.wish_list.action.WishListAction
import gortea.jgmax.wish_list.features.wish_list.event.WishListEvent
import gortea.jgmax.wish_list.features.wish_list.state.WishListState
import gortea.jgmax.wish_list.mvi.view.AppFragmentViewModel
import gortea.jgmax.wish_list.navigation.coordinator.Coordinator
import gortea.jgmax.wish_list.screens.wish_list.action.WishListViewAction
import gortea.jgmax.wish_list.screens.wish_list.data.WishData
import gortea.jgmax.wish_list.screens.wish_list.event.WishListViewEvent
import gortea.jgmax.wish_list.screens.wish_list.state.WishListViewState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class WishListViewModel @Inject constructor(
    featureFactory: FeatureFactory,
    private val coordinator: Coordinator
) : AppFragmentViewModel<WishListViewState, WishListViewEvent, WishListViewAction, WishListState, WishListEvent, WishListAction>() {
    override val mutableStateFlow = MutableStateFlow(WishListViewState.Default)
    override val mutableActionFlow = MutableSharedFlow<WishListViewAction?>()

    override val feature = featureFactory
        .createFeature<WishListState, WishListEvent, WishListAction>(viewModelScope)
        ?: throw IllegalStateException("Unknown feature")

    init {
        collectFeatureFlows()
    }

    override fun bindViewStateToFeatureState(state: WishListViewState): WishListState {
        return feature.stateFlow.value
    }

    override fun bindFeatureStateToViewState(state: WishListState): WishListViewState {
        return stateFlow.value.copy(
            isLoading = state.isLoading,
            list = state.list.map { WishData.fromModel(it) }
        )
    }

    override fun bindFeatureActionToViewAction(action: WishListAction): WishListViewAction? {
        return null
    }

    override fun bindViewEventToFeatureEvent(event: WishListViewEvent): WishListEvent? {
        return when (event) {
            is WishListViewEvent.GetList -> {
                WishListEvent.GetList
            }
            is WishListViewEvent.OnDeleteWishClick -> {
                WishListEvent.RemoveWish(event.url)
            }
            is WishListViewEvent.OnAddWishClick -> {
                coordinator.navigateToUpdateWish()
                null
            }
            is WishListViewEvent.OnUpdateWishClick -> {
                null
            }
            is WishListViewEvent.OnItemWishClick -> {
                sendViewAction(WishListViewAction.OpenUrlInBrowser(event.url))
                null
            }
        }
    }
}
