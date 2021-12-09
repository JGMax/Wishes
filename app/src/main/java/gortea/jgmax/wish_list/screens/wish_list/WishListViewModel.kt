package gortea.jgmax.wish_list.screens.wish_list

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gortea.jgmax.wish_list.features.factory.FeatureFactory
import gortea.jgmax.wish_list.features.wish_list.action.WishListAction
import gortea.jgmax.wish_list.features.wish_list.event.WishListEvent
import gortea.jgmax.wish_list.features.wish_list.state.WishListState
import gortea.jgmax.wish_list.mvi.view.AppViewModel
import gortea.jgmax.wish_list.navigation.coordinator.Coordinator
import gortea.jgmax.wish_list.screens.wish_list.action.WishListViewAction
import gortea.jgmax.wish_list.screens.wish_list.data.WishData
import gortea.jgmax.wish_list.screens.wish_list.event.WishListViewEvent
import gortea.jgmax.wish_list.screens.wish_list.state.WishListViewState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishListViewModel @Inject constructor(
    featureFactory: FeatureFactory,
    private val coordinator: Coordinator
) : AppViewModel<WishListViewState, WishListViewEvent, WishListViewAction, WishListState, WishListEvent, WishListAction>() {
    override val mutableStateFlow = MutableStateFlow(WishListViewState.Default)
    override val mutableActionFlow = MutableSharedFlow<WishListViewAction?>()
    private var listJob: Job? = null

    override val feature = featureFactory
        .createFeature<WishListState, WishListEvent, WishListAction>(viewModelScope)
        ?: throw IllegalStateException("Unknown feature")

    init {
        collectFeatureFlows()
        handleEvent(WishListViewEvent.GetListFlow)
    }

    override fun bindViewStateToFeatureState(state: WishListViewState): WishListState {
        return feature.stateFlow.value
    }

    override fun bindFeatureStateToViewState(state: WishListState): WishListViewState {
        return stateFlow.value.copy(isLoading = state.isLoading)
    }

    override fun bindFeatureActionToViewAction(action: WishListAction): WishListViewAction? {
        return when (action) {
            is WishListAction.CollectList -> {
                listJob?.cancel()
                listJob = viewModelScope.launch {
                    action.listFlow
                        .map { list -> list.map { WishData.fromModel(it) } }
                        .onEach { list -> handleState(stateFlow.value.copy(list = list)) }
                        .collect()
                }
                null
            }
            is WishListAction.EnqueueWorkRequest -> WishListViewAction.EnqueueWork(action.request)
            is WishListAction.ItemDeleted -> WishListViewAction.WishDeleted(action.wish)
        }
    }

    override fun bindViewEventToFeatureEvent(event: WishListViewEvent): WishListEvent? {
        return when (event) {
            is WishListViewEvent.GetListFlow -> {
                WishListEvent.GetListFlow
            }
            is WishListViewEvent.RefreshList -> {
                WishListEvent.RefreshList
            }
            is WishListViewEvent.OnDeleteWishClick -> {
                WishListEvent.RemoveWish(event.url)
            }
            is WishListViewEvent.AddItem -> {
                WishListEvent.AddWish(event.wish)
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
            is WishListViewEvent.OnPreferencesClick -> {
                coordinator.navigateToPreferences()
                null
            }
        }
    }
}
