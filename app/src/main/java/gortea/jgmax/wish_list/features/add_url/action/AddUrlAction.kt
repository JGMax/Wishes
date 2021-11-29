package gortea.jgmax.wish_list.features.add_url.action

import android.graphics.Bitmap
import androidx.annotation.StringRes
import gortea.jgmax.wish_list.mvi.domain.Action

sealed class AddUrlAction : Action {
    object UrlAdded : AddUrlAction()

    class ShowError(@StringRes val message: Int) : AddUrlAction()
    class RenderBitmap(val bitmap: Bitmap) : AddUrlAction()
}