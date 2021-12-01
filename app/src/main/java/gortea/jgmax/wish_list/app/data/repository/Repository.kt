package gortea.jgmax.wish_list.app.data.repository

import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel

interface Repository {
    suspend fun getWishes(): List<WishModel>
    suspend fun hasWishByUrl(url: String): Boolean
    suspend fun addWish(wish: WishModel)
    suspend fun updateWish(wish: WishModel)
    suspend fun deleteWish(wish: WishModel)
}