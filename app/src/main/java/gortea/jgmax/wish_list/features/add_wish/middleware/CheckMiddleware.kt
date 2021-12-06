package gortea.jgmax.wish_list.features.add_wish.middleware

import gortea.jgmax.wish_list.app.data.repository.Repository
import gortea.jgmax.wish_list.app.validator.ValidatorGroup
import gortea.jgmax.wish_list.features.add_wish.event.AddWishEvent
import gortea.jgmax.wish_list.features.add_wish.validators.CorrectUrlValidator
import gortea.jgmax.wish_list.features.add_wish.validators.NewUrlValidator
import gortea.jgmax.wish_list.features.add_wish.validators.WishModelParamsValidator
import gortea.jgmax.wish_list.features.add_wish.validators.WishModelValidator
import gortea.jgmax.wish_list.mvi.domain.DelayedEvent
import gortea.jgmax.wish_list.mvi.domain.Middleware

class CheckMiddleware(
    repository: Repository,
    delayedEvent: DelayedEvent<AddWishEvent>
) : Middleware<AddWishEvent> {
    private val wishModelValidator = ValidatorGroup(
        arrayOf(
            WishModelValidator(
                WishModelParamsValidator()
            )
        )
    )

    private val urlValidator = ValidatorGroup(
        arrayOf(
            CorrectUrlValidator(
                callback = { result, value ->
                    if (!result) {
                        delayedEvent.onEvent(AddWishEvent.CheckIncorrectUrl(value))
                    }
                }
            ),
            NewUrlValidator(
                repository,
                callback = { result, value ->
                    if (!result) {
                        delayedEvent.onEvent(AddWishEvent.CheckUrlAlreadyAdded(value))
                    }
                }
            )
        )
    )

    override suspend fun effect(event: AddWishEvent): AddWishEvent? {
        val newEvent: AddWishEvent? = when (event) {
            is AddWishEvent.CheckUrl -> {
                val url = event.url.trim()
                val urlIsValid = urlValidator.validate(url)
                if (urlIsValid) {
                    AddWishEvent.LoadUrl(url)
                } else {
                    null
                }

            }
            is AddWishEvent.CheckWish -> {
                val isValid = wishModelValidator.validate(event.wishModel)
                if (isValid) {
                    AddWishEvent.CheckWishSuccess(event.wishModel)
                } else {
                    AddWishEvent.CheckWishFailed(event.wishModel)
                }
            }
            else -> null
        }
        return newEvent
    }
}
