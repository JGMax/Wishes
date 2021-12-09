package gortea.jgmax.wish_list.screens.wish_list.action

import androidx.work.WorkRequest
import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import gortea.jgmax.wish_list.mvi.view.ViewAction

sealed class WishListViewAction : ViewAction {
    class OpenUrlInBrowser(val url: String) : WishListViewAction()
    class WishDeleted(val wish: WishModel) : WishListViewAction()
    class EnqueueWork(val request: WorkRequest) : WishListViewAction()
}
