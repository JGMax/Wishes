package gortea.jgmax.wish_list.screens.wish_list.list.item

import gortea.jgmax.wish_list.screens.wish_list.data.WishData

data class WishDataWrapper(
    val data: WishData,
    val onClick: (WishData) -> Unit,
    val viewType: Int
)
