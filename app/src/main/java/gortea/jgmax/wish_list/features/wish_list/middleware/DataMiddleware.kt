package gortea.jgmax.wish_list.features.wish_list.middleware

import android.util.Log
import gortea.jgmax.wish_list.app.data.repository.Repository
import gortea.jgmax.wish_list.features.wish_list.event.WishListEvent
import gortea.jgmax.wish_list.mvi.domain.DelayedEvent
import gortea.jgmax.wish_list.mvi.domain.Middleware
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DataMiddleware(
    private val repository: Repository,
    private val delayedEvent: DelayedEvent<WishListEvent>,
    private val coroutineScope: CoroutineScope
) : Middleware<WishListEvent> {
    override suspend fun effect(event: WishListEvent): WishListEvent? {
        val newEvent: WishListEvent? = when(event) {
            is WishListEvent.GetList -> {
                var isLoading = true
                coroutineScope.launch {
                    val list = repository.getWishes()
                    isLoading = false
                    delayedEvent.onEvent(WishListEvent.ReturnList(list))
                }
                if (isLoading) {
                    WishListEvent.Loading
                } else {
                    null
                }
            }
            is WishListEvent.RemoveWish -> {
                coroutineScope.launch {
                    repository.deleteWish(event.url)
                    delayedEvent.onEvent(WishListEvent.GetList)
                }
                null
            }
            else ->  null
        }
        return newEvent
    }
}