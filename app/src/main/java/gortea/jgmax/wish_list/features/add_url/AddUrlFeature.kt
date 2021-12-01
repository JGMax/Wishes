package gortea.jgmax.wish_list.features.add_url

import gortea.jgmax.wish_list.features.add_url.action.AddUrlAction
import gortea.jgmax.wish_list.features.add_url.event.AddUrlEvent
import gortea.jgmax.wish_list.features.add_url.middleware.AddWishMiddleware
import gortea.jgmax.wish_list.features.add_url.middleware.LoadUrlMiddleware
import gortea.jgmax.wish_list.features.add_url.middleware.RecognitionMiddleware
import gortea.jgmax.wish_list.features.add_url.reducer.AddUrlReducer
import gortea.jgmax.wish_list.features.add_url.state.AddUrlState
import gortea.jgmax.wish_list.mvi.data.DependencyStore
import gortea.jgmax.wish_list.mvi.domain.DelayedEvent
import gortea.jgmax.wish_list.mvi.domain.Feature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AddUrlFeature(
    store: DependencyStore,
    coroutineScope: CoroutineScope
) : Feature<AddUrlState, AddUrlEvent, AddUrlAction>(coroutineScope) {
    override val mutableStateFlow = MutableStateFlow(AddUrlState.Default)
    override val mutableEventFlow = MutableSharedFlow<AddUrlEvent>()
    override val mutableActionFlow = MutableSharedFlow<AddUrlAction>()

    private val delayedEvent = DelayedEvent<AddUrlEvent> {
        coroutineScope.launch {
            handleEvent(it, stateFlow.value)
        }
    }

    override val reducer = AddUrlReducer()
    override val middlewares = setOf(
        LoadUrlMiddleware(store.pageLoader, delayedEvent),
        RecognitionMiddleware(store.textRecognizer, delayedEvent),
        AddWishMiddleware(store.repository)
    )

    init {
        handleSideEvents()
    }
}