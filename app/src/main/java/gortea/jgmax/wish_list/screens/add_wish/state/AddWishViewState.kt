package gortea.jgmax.wish_list.screens.add_wish.state

import android.content.res.Resources
import android.view.View
import gortea.jgmax.wish_list.R
import gortea.jgmax.wish_list.mvi.view.ViewState
import gortea.jgmax.wish_list.screens.add_wish.data.WishData

data class AddWishViewState(
    val isLoading: Boolean,
    val loadingProgress: Int,
    val wish: WishData,
    val isUrlAccepted: Boolean?,
    val wasUrlAccepted: Boolean,
    val isWishAdded: Boolean?
) : ViewState {
    val isAcceptButtonEnabled: Boolean
        get() = wish.url.isNotEmpty()
    val acceptButtonText: Int
        get() = if (isUrlAccepted == true) R.string.add_btn_text else R.string.accept_btn_text
    val isFieldsVisible: Boolean
        get() = wasUrlAccepted
    val isReloadVisible: Boolean
        get() = isUrlAccepted == true

    fun getCurrentPriceValue(resources: Resources): String {
        val hint = resources.getString(R.string.current_price_field_hint)
        return if (wish.currentPrice.isEmpty())
            hint
        else
            "$hint: ${wish.currentPrice}"
    }


    companion object {
        val Default = AddWishViewState(
            isLoading = false,
            loadingProgress = 0,
            wish = WishData.Default,
            isUrlAccepted = null,
            wasUrlAccepted = false,
            isWishAdded = null
        )
    }
}
