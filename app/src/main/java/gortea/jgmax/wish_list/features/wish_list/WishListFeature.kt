package gortea.jgmax.wish_list.features.wish_list

import gortea.jgmax.wish_list.features.wish_list.action.WishListAction
import gortea.jgmax.wish_list.features.wish_list.event.WishListEvent
import gortea.jgmax.wish_list.features.wish_list.middleware.DataMiddleware
import gortea.jgmax.wish_list.features.wish_list.reducer.WishListReducer
import gortea.jgmax.wish_list.features.wish_list.state.WishListState
import gortea.jgmax.wish_list.mvi.data.DependencyStore
import gortea.jgmax.wish_list.mvi.domain.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.KClass

class WishListFeature(
    store: DependencyStore,
    coroutineScope: CoroutineScope
) : Feature<WishListState, WishListEvent, WishListAction>(coroutineScope) {
    override val mutableStateFlow = MutableStateFlow(WishListState.Default)
    override val mutableEventFlow = MutableSharedFlow<WishListEvent>(replay = 1)
    override val mutableActionFlow = MutableSharedFlow<WishListAction>()

    private val delayedEvent = DelayedEvent<WishListEvent> {
        handleEvent(it, null)
    }

    override val reducer = WishListReducer()
    override val middlewares = setOf(
        DataMiddleware(store.repository, delayedEvent, coroutineScope)
    )

    init {
        handleSideEvents()
    }

    companion object {
        inline fun <reified S : State, reified E : Event, reified A : Action> deals(
            stateType: KClass<S> = S::class,
            eventType: KClass<E> = E::class,
            actionType: KClass<A> = A::class
        ): Boolean {
            return stateType == WishListState::class
                    && eventType == WishListEvent::class
                    && actionType == WishListAction::class
        }
    }
}
