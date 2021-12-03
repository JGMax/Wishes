package gortea.jgmax.wish_list.features.select_data_zone

import gortea.jgmax.wish_list.features.select_data_zone.action.SelectDataZoneAction
import gortea.jgmax.wish_list.features.select_data_zone.event.SelectDataZoneEvent
import gortea.jgmax.wish_list.features.select_data_zone.middleware.LoadUrlMiddleware
import gortea.jgmax.wish_list.features.select_data_zone.middleware.RecognitionMiddleware
import gortea.jgmax.wish_list.features.select_data_zone.reducer.SelectDataZoneReducer
import gortea.jgmax.wish_list.features.select_data_zone.state.SelectDataZoneState
import gortea.jgmax.wish_list.mvi.data.DependencyStore
import gortea.jgmax.wish_list.mvi.domain.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.KClass

class SelectDataFeature(
    store: DependencyStore,
    coroutineScope: CoroutineScope
) : Feature<SelectDataZoneState, SelectDataZoneEvent, SelectDataZoneAction>(coroutineScope) {
    override val mutableStateFlow = MutableStateFlow(SelectDataZoneState.Default)
    override val mutableEventFlow = MutableSharedFlow<SelectDataZoneEvent>()
    override val mutableActionFlow = MutableSharedFlow<SelectDataZoneAction>()

    private val delayedEvent = DelayedEvent<SelectDataZoneEvent> {
        handleEvent(it, stateFlow.value)
    }

    override val reducer = SelectDataZoneReducer()
    override val middlewares = setOf(
        LoadUrlMiddleware(store.pageLoader, delayedEvent),
        RecognitionMiddleware(store.textRecognizer, delayedEvent)
    )

    init {
        handleSideEvents()
    }

    companion object {
        inline fun<reified S: State, reified E: Event, reified A: Action> deals(
            stateType: KClass<S> = S::class,
            eventType: KClass<E> = E::class,
            actionType: KClass<A> = A::class
        ): Boolean {
            return stateType == SelectDataZoneState::class
                    && eventType == SelectDataZoneEvent::class
                    && actionType == SelectDataZoneAction::class
        }
    }
}