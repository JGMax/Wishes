package gortea.jgmax.wish_list.mvi.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gortea.jgmax.wish_list.mvi.domain.Action
import gortea.jgmax.wish_list.mvi.domain.Event
import gortea.jgmax.wish_list.mvi.domain.Feature
import gortea.jgmax.wish_list.mvi.domain.State
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class AppFragmentViewModel<S : ViewState, E : ViewEvent, A : ViewAction, FS : State, FE : Event, FA : Action> :
    ViewModel() {
    protected abstract val mutableStateFlow: MutableStateFlow<S>
    val stateFlow: Flow<S>
        get() = mutableStateFlow

    protected abstract val mutableActionFlow: MutableSharedFlow<A?>
    val actionFlow: Flow<A>
        get() = mutableActionFlow.filterNotNull()

    protected abstract val feature: Feature<FS, FE, FA>
    protected abstract fun bindViewStateToFeatureState(state: S): FS
    protected abstract fun bindFeatureStateToViewState(state: FS): S
    protected abstract fun bindFeatureActionToViewAction(action: FA): A?
    protected abstract fun bindViewEventToFeatureEvent(event: E): FE?

    protected fun collectFeatureAction() {
        viewModelScope.launch {
            feature.actionFlow
                .filterNotNull()
                .collect { handleFeatureAction(it) }
        }
    }

    protected fun collectFeatureState() {
        viewModelScope.launch {
            feature.stateFlow
                .filterNotNull()
                .collect { handleFeatureState(it) }
        }
    }

    private suspend fun handleFeatureAction(action: FA) {
        mutableActionFlow.emit(bindFeatureActionToViewAction(action))
    }

    private suspend fun handleFeatureState(state: FS) {
        mutableStateFlow.emit(bindFeatureStateToViewState(state))
    }

    fun handleEvent(event: E) {
        bindViewEventToFeatureEvent(event)?.let{ handleEvent(it) }
    }

    private fun handleEvent(event: FE) {
        viewModelScope.launch {
            feature.handleEvent(event, bindViewStateToFeatureState(mutableStateFlow.value))
        }
    }
}