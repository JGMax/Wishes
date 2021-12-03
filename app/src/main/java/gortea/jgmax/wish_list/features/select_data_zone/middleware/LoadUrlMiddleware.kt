package gortea.jgmax.wish_list.features.select_data_zone.middleware

import gortea.jgmax.wish_list.app.data.remote.loader.PageLoader
import gortea.jgmax.wish_list.features.select_data_zone.event.SelectDataZoneEvent
import gortea.jgmax.wish_list.mvi.domain.DelayedEvent
import gortea.jgmax.wish_list.mvi.domain.Middleware

class LoadUrlMiddleware(
    private val pageLoader: PageLoader,
    private val delayedEvent: DelayedEvent<SelectDataZoneEvent>
) : Middleware<SelectDataZoneEvent> {
    override suspend fun effect(event: SelectDataZoneEvent): SelectDataZoneEvent? {
        val newEvent: SelectDataZoneEvent? = when (event) {
            is SelectDataZoneEvent.LoadUrl, is SelectDataZoneEvent.ReloadUrl -> {
                val url =
                    if (event is SelectDataZoneEvent.LoadUrl) event.url else (event as SelectDataZoneEvent.ReloadUrl).url
                var isLoading = true
                pageLoader.attachListeners(
                    onComplete = { page ->
                        isLoading = false
                        delayedEvent.onEvent(SelectDataZoneEvent.BitmapLoaded(url, page))
                    },
                    onError = {
                        isLoading = false
                        delayedEvent.onEvent(SelectDataZoneEvent.LoadingFailed)
                    },
                    onProgress = {
                        delayedEvent.onEvent(SelectDataZoneEvent.LoadingInProgress(it))
                    }
                )
                pageLoader.loadAsBitmap(url = url, force = event is SelectDataZoneEvent.ReloadUrl)
                if (isLoading) {
                    SelectDataZoneEvent.Loading
                } else {
                    null
                }
            }
            else -> null
        }
        return newEvent
    }
}
