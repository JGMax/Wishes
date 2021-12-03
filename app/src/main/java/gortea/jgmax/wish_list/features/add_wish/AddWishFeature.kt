package gortea.jgmax.wish_list.features.add_wish

import gortea.jgmax.wish_list.features.add_wish.action.AddWishAction
import gortea.jgmax.wish_list.features.add_wish.event.AddWishEvent
import gortea.jgmax.wish_list.features.add_wish.middleware.AcceptUrlMiddleware
import gortea.jgmax.wish_list.features.add_wish.middleware.AddWishMiddleware
import gortea.jgmax.wish_list.features.add_wish.middleware.CheckMiddleware
import gortea.jgmax.wish_list.features.add_wish.middleware.LoadMiddleware
import gortea.jgmax.wish_list.features.add_wish.reducer.AddWishReducer
import gortea.jgmax.wish_list.features.add_wish.state.AddWishState
import gortea.jgmax.wish_list.mvi.data.DependencyStore
import gortea.jgmax.wish_list.mvi.domain.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.KClass

class AddWishFeature(
    store: DependencyStore,
    coroutineScope: CoroutineScope
) : Feature<AddWishState, AddWishEvent, AddWishAction>(coroutineScope) {
    override val mutableStateFlow = MutableStateFlow(AddWishState.Default)
    override val mutableEventFlow = MutableSharedFlow<AddWishEvent>()
    override val mutableActionFlow = MutableSharedFlow<AddWishAction>()
    private val delayedEvent = DelayedEvent<AddWishEvent> {
        handleEvent(it, stateFlow.value)
    }

    override val reducer = AddWishReducer()
    override val middlewares = setOf(
        AcceptUrlMiddleware(),
        AddWishMiddleware(store.repository),
        CheckMiddleware(store.repository, delayedEvent),
        LoadMiddleware(store.pageLoader, delayedEvent)
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
            return stateType == AddWishState::class
                    && eventType == AddWishEvent::class
                    && actionType == AddWishAction::class
        }
    }
}