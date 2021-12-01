package gortea.jgmax.wish_list.mvi.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gortea.jgmax.wish_list.mvi.domain.Action
import gortea.jgmax.wish_list.mvi.domain.Event
import gortea.jgmax.wish_list.mvi.domain.Feature
import gortea.jgmax.wish_list.mvi.domain.State
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class AppFragmentViewModel<VS : ViewState, VE : ViewEvent, VA : ViewAction, FS : State, FE : Event, FA : Action> :
    ViewModel() {
    protected abstract val mutableStateFlow: MutableStateFlow<VS>
    val stateFlow: StateFlow<VS>
        get() = mutableStateFlow

    protected abstract val mutableActionFlow: MutableSharedFlow<VA?>
    val actionFlow: SharedFlow<VA?>
        get() = mutableActionFlow

    protected abstract val feature: Feature<FS, FE, FA>
    protected abstract fun bindViewStateToFeatureState(state: VS): FS
    protected abstract fun bindFeatureStateToViewState(state: FS): VS
    protected abstract fun bindFeatureActionToViewAction(action: FA): VA?
    protected abstract fun bindViewEventToFeatureEvent(event: VE): FE?

    protected fun collectFeatureFlows() {
        viewModelScope.launch {
            launch {
                feature.actionFlow
                    .filterNotNull()
                    .map { bindFeatureActionToViewAction(it) }
                    .onEach { mutableActionFlow.emit(it) }
                    .collect()
            }

            launch {
                feature.stateFlow
                    .filterNotNull()
                    .map { bindFeatureStateToViewState(it) }
                    .onEach { mutableStateFlow.emit(it) }
                    .collect()
            }
        }
    }

    fun handleEvent(event: VE) {
        bindViewEventToFeatureEvent(event)?.let { handleEvent(it) }
    }

    fun handleState(state: VS) {
        viewModelScope.launch {
            mutableStateFlow.emit(state)
        }
    }

    private fun handleEvent(event: FE) {
        viewModelScope.launch {
            feature.handleEvent(event, bindViewStateToFeatureState(mutableStateFlow.value))
        }
    }
}