package gortea.jgmax.wish_list.features.add_wish.middleware

import android.util.Log
import gortea.jgmax.wish_list.app.data.repository.Repository
import gortea.jgmax.wish_list.features.add_wish.event.AddWishEvent
import gortea.jgmax.wish_list.mvi.domain.Middleware
import kotlinx.coroutines.CoroutineScope

class AddWishMiddleware(
    private val repository: Repository
) : Middleware<AddWishEvent> {
    override suspend fun effect(event: AddWishEvent): AddWishEvent? {
        val newEvent: AddWishEvent? = when (event) {
            is AddWishEvent.AddWish -> {
                AddWishEvent.CheckWish(event.wishModel)
            }
            is AddWishEvent.CheckWishSuccess -> {
                repository.addWish(event.wishModel)
                AddWishEvent.AddSuccessful(event.wishModel)
            }
            else -> null
        }
        return newEvent
    }
}