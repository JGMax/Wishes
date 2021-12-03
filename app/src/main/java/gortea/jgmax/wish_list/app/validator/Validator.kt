package gortea.jgmax.wish_list.app.validator

interface Validator<in T> {
    fun validate(value: T): Boolean
}