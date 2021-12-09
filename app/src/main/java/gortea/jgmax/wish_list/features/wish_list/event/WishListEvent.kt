package gortea.jgmax.wish_list.features.wish_list.event

import androidx.work.WorkRequest
import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import gortea.jgmax.wish_list.mvi.domain.Event
import kotlinx.coroutines.flow.Flow

sealed class WishListEvent : Event {
    // View Events
    object GetListFlow : WishListEvent()
    object RefreshList : WishListEvent()
    object GetList : WishListEvent()

    class AddWish(val wishModel: WishModel) : WishListEvent()
    class RemoveWish(val url: String) : WishListEvent()

    // Side Events
    object Loading : WishListEvent()

    class EnqueueWork(val request: WorkRequest) : WishListEvent()
    class WishRemoved(val wishModel: WishModel) : WishListEvent()

    class ReturnList(val list: List<WishModel>) : WishListEvent()
    class ReturnFlow(val list: Flow<List<WishModel>>) : WishListEvent()
}
