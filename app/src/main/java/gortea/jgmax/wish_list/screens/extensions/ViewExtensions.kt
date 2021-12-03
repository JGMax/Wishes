package gortea.jgmax.wish_list.screens.extensions

import android.text.InputType
import android.util.Log
import android.widget.EditText

fun EditText.setReadOnly(value: Boolean, inputType: Int = InputType.TYPE_NULL) {
    isFocusable = !value
    isFocusableInTouchMode = !value
    this.inputType = inputType
}

fun EditText.setTextIfNoFocus(text: CharSequence) {
    if (!hasFocus()) {
        setText(text)
    }
}
