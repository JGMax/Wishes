package gortea.jgmax.wish_list.features.add_wish.event

import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import gortea.jgmax.wish_list.mvi.domain.Event


sealed class AddWishEvent : Event {
    // View Events
    class AcceptUrl(val url: String) : AddWishEvent()
    class AddWish(val wishModel: WishModel) : AddWishEvent()

    // Side Events
    object Loading : AddWishEvent()
    object LoadingFailed : AddWishEvent()

    class CheckWishFailed(val wishModel: WishModel) : AddWishEvent()
    class CheckUrlAlreadyAdded(val url: String) : AddWishEvent()
    class CheckIncorrectUrl(val url: String) : AddWishEvent()
    class LoadingInProcess(val progress: Int) : AddWishEvent()

    class LoadUrl(val url: String) : AddWishEvent()
    class AddSuccessful(val wishModel: WishModel) : AddWishEvent()
    class CheckWishSuccess(val wishModel: WishModel) : AddWishEvent()
    class CheckWish(val wishModel: WishModel) : AddWishEvent()
    class CheckUrl(val url: String) : AddWishEvent()
    class LoadingUrlSuccess(val url: String) : AddWishEvent()
}