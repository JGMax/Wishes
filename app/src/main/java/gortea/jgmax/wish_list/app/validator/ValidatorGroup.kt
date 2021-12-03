package gortea.jgmax.wish_list.app.validator

open class ValidatorGroup<T>(initialValidators: Array<Validator<T>> = arrayOf()) : Validator<T> {
    private val validators = mutableListOf(*initialValidators)

    fun add(validator: Validator<T>): ValidatorGroup<T> {
        if (!validators.contains(validator)) {
            validators.add(validator)
        }
        return this
    }

    fun remove(validator: Validator<T>): ValidatorGroup<T> {
        validators.remove(validator)
        return this
    }

    fun clear(): ValidatorGroup<T> {
        validators.clear()
        return this
    }

    override fun validate(value: T): Boolean {
        return validators.all { it.validate(value) }
    }
}