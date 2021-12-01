package gortea.jgmax.wish_list.features.add_url.middleware

import gortea.jgmax.wish_list.app.data.repository.Repository
import gortea.jgmax.wish_list.features.add_url.event.AddUrlEvent
import gortea.jgmax.wish_list.mvi.domain.Middleware

class AddWishMiddleware(
    private val repository: Repository
) : Middleware<AddUrlEvent> {
    override suspend fun effect(event: AddUrlEvent): AddUrlEvent? {
        val newEvent: AddUrlEvent? = when (event) {
            is AddUrlEvent.CheckUrl -> {
                if (repository.hasWishByUrl(event.url)) {
                    AddUrlEvent.UrlAlreadyAdded
                } else {
                    AddUrlEvent.AddNewUrl(event.url)
                }
            }
            is AddUrlEvent.AddWish -> {
                repository.addWish(event.model)
                null
            }
            else -> null
        }
        return newEvent
    }
}