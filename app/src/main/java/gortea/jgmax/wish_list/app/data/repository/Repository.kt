package gortea.jgmax.wish_list.app.data.repository

import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel

interface Repository {
    suspend fun getWishes(): List<WishModel>
    suspend fun hasWishByUrl(url: String): Boolean
    suspend fun getWish(url: String): WishModel?
    suspend fun addWish(wish: WishModel): Long
    suspend fun updateWish(wish: WishModel)
    suspend fun deleteWish(url: String)
}