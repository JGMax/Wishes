package gortea.jgmax.wish_list.features.add_wish.validators

import gortea.jgmax.wish_list.app.data.repository.models.wish.Params
import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import gortea.jgmax.wish_list.app.validator.Validator

class WishModelValidator(
    private val paramsValidator: Validator<Params>
) : Validator<WishModel> {
    override fun validate(value: WishModel): Boolean {
        return value.run {
            currentPrice != null
                    && title.isNotEmpty()
                    && url.isNotEmpty()
                    && paramsValidator.validate(params)
        }
    }
}
