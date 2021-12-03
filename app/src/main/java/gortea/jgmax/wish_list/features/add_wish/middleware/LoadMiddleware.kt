package gortea.jgmax.wish_list.features.add_wish.middleware

import gortea.jgmax.wish_list.app.data.remote.loader.PageLoader
import gortea.jgmax.wish_list.features.add_wish.event.AddWishEvent
import gortea.jgmax.wish_list.mvi.domain.DelayedEvent
import gortea.jgmax.wish_list.mvi.domain.Middleware

class LoadMiddleware(
    private val loader: PageLoader,
    private val delayedEvent: DelayedEvent<AddWishEvent>
) : Middleware<AddWishEvent> {
    override suspend fun effect(event: AddWishEvent): AddWishEvent? {
        val newEvent: AddWishEvent? = when (event) {
            is AddWishEvent.LoadUrl, is AddWishEvent.ReloadUrl -> {
                val url = if(event is AddWishEvent.LoadUrl) event.url else (event as AddWishEvent.ReloadUrl).url
                var isLoading = true
                loader.attachListeners(
                    onComplete = {
                        isLoading = false
                        delayedEvent.onEvent(AddWishEvent.LoadingUrlSuccess(url))
                    },
                    onError = {
                        isLoading = false
                        delayedEvent.onEvent(AddWishEvent.LoadingFailed)
                    },
                    onProgress = { delayedEvent.onEvent(AddWishEvent.LoadingInProcess(it)) }
                )
                loader.loadAsBitmap(url = url, force = event is AddWishEvent.ReloadUrl)
                if (isLoading) {
                    AddWishEvent.Loading
                } else {
                    null
                }
            }
            else -> null
        }
        return newEvent
    }
}