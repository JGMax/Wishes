package gortea.jgmax.wish_list.features.add_wish.validators

import gortea.jgmax.wish_list.app.data.repository.Repository
import gortea.jgmax.wish_list.app.validator.Validator
import kotlinx.coroutines.runBlocking

class NewUrlValidator(
    private val repository: Repository,
    private val callback: (Boolean, String) -> Unit = {_, _ ->}
) : Validator<String> {
    override fun validate(value: String): Boolean {
        val result = runBlocking { !repository.hasWishByUrl(value) }
        callback(result, value)
        return result
    }
}
