package gortea.jgmax.wish_list.mvi.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class Feature<S : State, E : Event, A : Action>(
    private val coroutineScope: CoroutineScope
) {
    protected abstract val mutableStateFlow: MutableStateFlow<S>
    val stateFlow: StateFlow<S>
        get() = mutableStateFlow
    protected abstract val mutableEventFlow: MutableSharedFlow<E>
    protected abstract val mutableActionFlow: MutableSharedFlow<A>
    val actionFlow: SharedFlow<A>
        get() = mutableActionFlow

    protected abstract val reducer: Reducer<S, E, A>
    protected abstract val middlewares: Set<Middleware<E>>

    protected fun handleSideEvents() {
        coroutineScope.launch {
            mutableEventFlow
                .buffer()
                .collect { event ->
                    coroutineScope.launch {
                        middlewares.forEach {
                            it.effect(event)?.let { newEvent ->
                                mutableEventFlow.emit(newEvent)
                            }
                        }
                    }
                    val reduced = reducer.reduce(event, stateFlow.value)
                    reduced.first?.let { mutableStateFlow.emit(it) }
                    reduced.second?.let { mutableActionFlow.emit(it) }
                }
        }
    }

    fun handleEvent(event: E, state: S?) {
        coroutineScope.launch {
            mutableEventFlow.emit(event)
            state?.let { mutableStateFlow.emit(state) }
        }
    }

    fun handleState(state: S) {
        coroutineScope.launch {
            mutableStateFlow.emit(state)
        }
    }
}
