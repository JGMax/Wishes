package gortea.jgmax.wish_list.features.select_data_zone.middleware

import gortea.jgmax.wish_list.app.data.repository.Repository
import gortea.jgmax.wish_list.features.select_data_zone.event.SelectDataZoneEvent
import gortea.jgmax.wish_list.mvi.domain.DelayedEvent
import gortea.jgmax.wish_list.mvi.domain.Middleware
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DataMiddleware(
    private val repository: Repository,
    private val delayedEvent: DelayedEvent<SelectDataZoneEvent>,
    private val coroutineScope: CoroutineScope
) : Middleware<SelectDataZoneEvent> {
    override suspend fun effect(event: SelectDataZoneEvent): SelectDataZoneEvent? {
        val newEvent: SelectDataZoneEvent? = when (event) {
            is SelectDataZoneEvent.GetWish -> {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        val wish = repository.getWish(event.url)
                        if (wish == null) {
                            delayedEvent.onEvent(SelectDataZoneEvent.UnknownWish)
                        } else {
                            delayedEvent.onEvent(SelectDataZoneEvent.ReturnWish(wish))
                        }
                    }
                }
                null
            }
            is SelectDataZoneEvent.UpdateWish -> {
                coroutineScope.launch {
                    repository.updateWish(event.wishModel)
                    delayedEvent.onEvent(SelectDataZoneEvent.WishUpdated)
                }
                null
            }
            else -> null
        }
        return newEvent
    }
}
