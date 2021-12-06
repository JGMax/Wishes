package gortea.jgmax.wish_list.screens.add_wish

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gortea.jgmax.wish_list.R
import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import gortea.jgmax.wish_list.features.add_wish.action.AddWishAction
import gortea.jgmax.wish_list.features.add_wish.event.AddWishEvent
import gortea.jgmax.wish_list.features.add_wish.state.AddWishState
import gortea.jgmax.wish_list.features.factory.FeatureFactory
import gortea.jgmax.wish_list.mvi.view.AppFragmentViewModel
import gortea.jgmax.wish_list.navigation.coordinator.Coordinator
import gortea.jgmax.wish_list.screens.add_wish.action.AddWishViewAction
import gortea.jgmax.wish_list.screens.add_wish.data.WishData
import gortea.jgmax.wish_list.screens.add_wish.event.AddWishViewEvent
import gortea.jgmax.wish_list.screens.add_wish.state.AddWishViewState
import gortea.jgmax.wish_list.screens.select_data_zone.data.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddWishViewModel @Inject constructor(
    featureFactory: FeatureFactory,
    private val coordinator: Coordinator
) : AppFragmentViewModel<AddWishViewState, AddWishViewEvent, AddWishViewAction, AddWishState, AddWishEvent, AddWishAction>() {
    override val mutableStateFlow = MutableStateFlow(AddWishViewState.Default)
    override val mutableActionFlow = MutableSharedFlow<AddWishViewAction?>()
    override val feature = featureFactory
        .createFeature<AddWishState, AddWishEvent, AddWishAction>(viewModelScope)
        ?: throw IllegalAccessException("Unknown feature")

    private var url: String? = null

    init {
        collectFeatureFlows()
        collectDetectionResult()
        synchronizeState()
    }

    private fun checkUrlChange(state: AddWishViewState): AddWishViewState {
        var checkedState = state
        if (url != state.wish.url) {
            checkedState = state.copy(
                isUrlAccepted = null,
                wish = state.wish.copy(
                    currentPrice = "",
                    position = null
                )
            )
            url = state.wish.url
        }
        return checkedState
    }

    private fun createWishData(url: String?, wish: WishModel?): WishData {
        var wishData: WishData? = wish?.let { WishData.fromModel(it) }
        if (wishData == null) {
            wishData = if (url.isNullOrEmpty()) {
                WishData.Default
            } else {
                WishData.Default.copy(url = url)
            }
        }
        return wishData
    }

    private fun collectDetectionResult() {
        viewModelScope.launch {
            launch {
                coordinator.getResult<Result>(Coordinator.DETECTION_RESULT)
                    ?.collect { result ->
                        handleState(
                            stateFlow.value.run {
                                copy(
                                    wish = wish.copy(
                                        currentPrice = result.value,
                                        position = result.position
                                    )
                                )
                            }
                        )
                    }
            }
        }
    }

    private fun synchronizeState() {
        viewModelScope.launch {
            stateFlow
                .filterNotNull()
                .map { bindViewStateToFeatureState(it) }
                .onEach { feature.handleState(it) }
                .collect()
        }
    }

    override fun bindViewStateToFeatureState(state: AddWishViewState): AddWishState {
        val checkedState = checkUrlChange(state)
        return AddWishState(
            isLoading = checkedState.isLoading,
            loadingProgress = checkedState.loadingProgress,
            url = checkedState.wish.url,
            isUrlAccepted = checkedState.isUrlAccepted,
            wish = checkedState.wish.toModel(),
            isWishAdded = checkedState.isWishAdded
        )
    }

    override fun bindFeatureStateToViewState(state: AddWishState): AddWishViewState {
        val wasUrlAccepted =
            if (stateFlow.value.wasUrlAccepted) true else state.isUrlAccepted ?: false
        url = state.url
        return stateFlow.value.copy(
            isLoading = state.isLoading,
            loadingProgress = state.loadingProgress,
            wish = createWishData(state.url, state.wish),
            isUrlAccepted = state.isUrlAccepted,
            isWishAdded = state.isWishAdded,
            wasUrlAccepted = wasUrlAccepted
        )
    }

    override fun bindFeatureActionToViewAction(action: AddWishAction): AddWishViewAction? {
        return when (action) {
            is AddWishAction.UrlAlreadyAdded -> {
                AddWishViewAction.ShowMessage(R.string.url_already_added)
            }
            is AddWishAction.WishSuccessfulAdded -> {
                coordinator.navigateToWishList()
                null
            }
            is AddWishAction.WishCheckFailed -> {
                AddWishViewAction.ShowMessage(R.string.wish_check_error)
            }
            is AddWishAction.IncorrectUrl -> {
                AddWishViewAction.ShowMessage(R.string.incorrect_url)
            }
            is AddWishAction.LoadingUrlFailed -> {
                AddWishViewAction.ShowMessage(R.string.loading_failed)
            }
            else -> null
        }
    }

    override fun bindViewEventToFeatureEvent(event: AddWishViewEvent): AddWishEvent? {
        val checkedState = checkUrlChange(stateFlow.value)
        return when (event) {
            is AddWishViewEvent.OnAcceptClick -> {
                if (checkedState.isUrlAccepted == true) {
                    AddWishEvent.AddWish(checkedState.wish.toModel())
                } else {
                    AddWishEvent.AcceptUrl(checkedState.wish.url)
                }
            }
            is AddWishViewEvent.OnPriceSelectionClick -> {
                if (checkedState.isUrlAccepted == true) {
                    coordinator.navigateToSelectDataZone(
                        checkedState.wish.url,
                        checkedState.isLoading,
                        checkedState.loadingProgress
                    )
                } else {
                    sendViewAction(AddWishViewAction.ShowMessage(R.string.accept_url_message))
                }
                null
            }
            is AddWishViewEvent.LoadUrl -> {
                if (checkedState.isUrlAccepted == true) {
                    AddWishEvent.LoadUrl(checkedState.wish.url)
                } else {
                    null
                }
            }
            is AddWishViewEvent.ReloadUrl -> {
                if (checkedState.isUrlAccepted == true) {
                    AddWishEvent.ReloadUrl(checkedState.wish.url)
                } else {
                    null
                }
            }
        }
    }
}
