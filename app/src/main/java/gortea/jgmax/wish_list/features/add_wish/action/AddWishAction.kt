package gortea.jgmax.wish_list.features.add_wish.action

import gortea.jgmax.wish_list.mvi.domain.Action

sealed class AddWishAction : Action {
    object WishSuccessfulAdded : AddWishAction()
    object UrlAlreadyAdded : AddWishAction()
    object IncorrectUrl : AddWishAction()
    object WishCheckFailed : AddWishAction()
    object UrlIsLoaded : AddWishAction()
    object LoadingUrlFailed : AddWishAction()
}
