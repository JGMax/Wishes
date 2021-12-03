package gortea.jgmax.wish_list.features.add_wish.middleware

import gortea.jgmax.wish_list.features.add_wish.event.AddWishEvent
import gortea.jgmax.wish_list.mvi.domain.Middleware

class AcceptUrlMiddleware : Middleware<AddWishEvent> {
    override suspend fun effect(event: AddWishEvent): AddWishEvent? {
        val newEvent: AddWishEvent? = when (event) {
            is AddWishEvent.AcceptUrl -> {
                AddWishEvent.CheckUrl(event.url)
            }
            else -> null
        }
        return newEvent
    }
}