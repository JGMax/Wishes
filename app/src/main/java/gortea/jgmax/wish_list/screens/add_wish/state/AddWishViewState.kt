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
    val currentPriceHintVisibility: Int
        get() = if(wasUrlAccepted && wish.currentPrice.isNotEmpty()) View.VISIBLE else View.INVISIBLE

    fun getCurrentPriceValue(resources: Resources): String {
        return if (wish.currentPrice.isEmpty())
            resources.getString(R.string.current_price_field_hint)
        else
            wish.currentPrice
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
