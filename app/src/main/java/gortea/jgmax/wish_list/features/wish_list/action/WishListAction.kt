package gortea.jgmax.wish_list.features.wish_list.action

import androidx.work.WorkRequest
import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import gortea.jgmax.wish_list.mvi.domain.Action
import kotlinx.coroutines.flow.Flow

sealed class WishListAction : Action {
    class ItemDeleted(val wish: WishModel) : WishListAction()
    class CollectList(val listFlow: Flow<List<WishModel>>) : WishListAction()
    class EnqueueWorkRequest(val request: WorkRequest) : WishListAction()
}
