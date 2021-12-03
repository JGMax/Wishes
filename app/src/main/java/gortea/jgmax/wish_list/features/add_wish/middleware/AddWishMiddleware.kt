package gortea.jgmax.wish_list.features.add_wish.middleware

import gortea.jgmax.wish_list.app.data.repository.Repository
import gortea.jgmax.wish_list.features.add_wish.event.AddWishEvent
import gortea.jgmax.wish_list.mvi.domain.Middleware
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AddWishMiddleware(
    private val repository: Repository,
    private val coroutineScope: CoroutineScope
) : Middleware<AddWishEvent> {
    override suspend fun effect(event: AddWishEvent): AddWishEvent? {
        val newEvent: AddWishEvent? = when(event) {
            is AddWishEvent.AddWish -> {
               AddWishEvent.CheckWish(event.wishModel)
            }
            is AddWishEvent.CheckWishSuccess -> {
                coroutineScope.launch {
                    repository.addWish(event.wishModel)
                }
                AddWishEvent.AddSuccessful(event.wishModel)
            }
            else -> null
        }
        return newEvent
    }
}