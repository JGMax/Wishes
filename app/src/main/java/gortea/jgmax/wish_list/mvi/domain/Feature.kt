package gortea.jgmax.wish_list.mvi.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class Feature<S : State, E : Event, A : Action>(
    private val coroutineScope: CoroutineScope
) {
    protected abstract val mutableStateFlow: MutableStateFlow<S>
    val stateFlow: Flow<S>
        get() = mutableStateFlow
    protected abstract val mutableEventFlow: MutableSharedFlow<E>
    protected abstract val mutableActionFlow: MutableSharedFlow<A>
    val actionFlow: Flow<A>
        get() = mutableActionFlow

    protected abstract val reducer: Reducer<S, E, A>
    protected abstract val middlewares: Set<Middleware<E>>

    protected fun handleSideEvents() {
        coroutineScope.launch {
            mutableEventFlow
                .buffer()
                .collect { event ->
                    middlewares.forEach {
                        it.effect(event)?.let { newEvent ->
                            coroutineScope.launch {
                                mutableEventFlow.emit(newEvent)
                            }
                        }
                    }
                    val reduced = reducer.reduce(event, mutableStateFlow.value)
                    reduced.first?.let { mutableStateFlow.emit(it) }
                    reduced.second?.let { mutableActionFlow.emit(it) }
                }
        }
    }

    suspend fun handleEvent(event: E, state: S) {
        mutableEventFlow.emit(event)
        mutableStateFlow.emit(state)
    }
}