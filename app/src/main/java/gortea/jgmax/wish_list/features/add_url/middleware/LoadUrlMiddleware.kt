package gortea.jgmax.wish_list.features.add_url.middleware

import gortea.jgmax.wish_list.app.data.remote.loader.PageLoader
import gortea.jgmax.wish_list.app.data.repository.Repository
import gortea.jgmax.wish_list.features.add_url.event.AddUrlEvent
import gortea.jgmax.wish_list.mvi.domain.DelayedEvent
import gortea.jgmax.wish_list.mvi.domain.Middleware

class LoadUrlMiddleware(
    private val pageLoader: PageLoader,
    private val delayedEvent: DelayedEvent<AddUrlEvent>
) : Middleware<AddUrlEvent> {
    override suspend fun effect(event: AddUrlEvent): AddUrlEvent? {
        val newEvent: AddUrlEvent? = when (event) {
            is AddUrlEvent.AddUrl -> {
                AddUrlEvent.CheckUrl(event.url)
            }
            is AddUrlEvent.AddNewUrl -> {
                var hasDelayedEvent = false
                pageLoader.loadAsBitmap(
                    url = event.url,
                    onComplete = { page ->
                        hasDelayedEvent = true
                        delayedEvent.onEvent(AddUrlEvent.BitmapLoaded(event.url, page))
                    },
                    onError = {
                        hasDelayedEvent = true
                        delayedEvent.onEvent(AddUrlEvent.LoadingFailed(event.url))
                    },
                    onProgress = {
                        delayedEvent.onEvent(AddUrlEvent.LoadingInProgress(event.url, it))
                    },
                    withImages = true
                )
                if (!hasDelayedEvent) {
                    AddUrlEvent.LoadingStarted(event.url)
                } else {
                    null
                }
            }
            else -> null
        }
        return newEvent
    }
}
