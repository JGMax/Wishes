package gortea.jgmax.wish_list.features.add_wish.validators

import gortea.jgmax.wish_list.app.data.repository.models.wish.Params
import gortea.jgmax.wish_list.app.validator.Validator

class WishModelParamsValidator : Validator<Params> {
    override fun validate(value: Params): Boolean {
        return value.run { position != null && targetPrice != null && initialPrice != null }
    }
}
