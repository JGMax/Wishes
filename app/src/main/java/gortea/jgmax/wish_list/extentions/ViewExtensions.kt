package gortea.jgmax.wish_list.extentions

import android.widget.EditText

fun EditText.setTextIfNoFocus(text: CharSequence) {
    if (!hasFocus()) {
        setText(text)
    }
}
