package gortea.jgmax.wish_list.features.wish_list.middleware

import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import gortea.jgmax.wish_list.app.data.repository.Repository
import gortea.jgmax.wish_list.features.wish_list.event.WishListEvent
import gortea.jgmax.wish_list.mvi.domain.DelayedEvent
import gortea.jgmax.wish_list.mvi.domain.Middleware
import gortea.jgmax.wish_list.workers.UpdateDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DataMiddleware(
    private val repository: Repository,
    private val delayedEvent: DelayedEvent<WishListEvent>,
    private val coroutineScope: CoroutineScope
) : Middleware<WishListEvent> {
    override suspend fun effect(event: WishListEvent): WishListEvent? {
        val newEvent: WishListEvent? = when (event) {
            is WishListEvent.GetListFlow -> {
                var isLoading = true
                coroutineScope.launch {
                    val flow = repository.getWishesFlow()
                    isLoading = false
                    delayedEvent.onEvent(WishListEvent.ReturnFlow(flow))
                }
                if (isLoading) {
                    WishListEvent.Loading
                } else {
                    null
                }
            }
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
                    val wish = repository.getWish(event.url)
                    repository.deleteWish(event.url)
                    wish?.let { delayedEvent.onEvent(WishListEvent.WishRemoved(it)) }
                }
                null
            }
            is WishListEvent.AddWish -> {
                coroutineScope.launch {
                    repository.addWish(event.wishModel)
                }
                null
            }
            is WishListEvent.RefreshList -> {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresStorageNotLow(true)
                    .setRequiresBatteryNotLow(true)
                    .build()
                val request =
                    OneTimeWorkRequestBuilder<UpdateDataWorker>()
                        .setConstraints(constraints)
                        .build()
                WishListEvent.EnqueueWork(request)
            }
            else -> null
        }
        return newEvent
    }
}
