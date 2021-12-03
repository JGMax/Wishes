package gortea.jgmax.wish_list.features.add_wish.validators

import android.util.Patterns
import gortea.jgmax.wish_list.app.validator.Validator

class CorrectUrlValidator(
    private val callback: (Boolean, String) -> Unit = { _, _ -> }
) : Validator<String> {
    override fun validate(value: String): Boolean {
        val result = Patterns.WEB_URL.matcher(value).matches()
        callback(result, value)
        return result
    }
}
