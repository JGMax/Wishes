package gortea.jgmax.wish_list.screens.extensions

import android.text.Editable

fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
