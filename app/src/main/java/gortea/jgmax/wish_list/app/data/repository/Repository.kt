package gortea.jgmax.wish_list.app.data.repository

import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun getWishesFlow(): Flow<List<WishModel>>
    suspend fun hasWishByUrl(url: String): Boolean
    suspend fun getWish(url: String): WishModel?
    suspend fun addWish(wish: WishModel): Long
    suspend fun updateWish(wish: WishModel)
    suspend fun deleteWish(url: String)
    suspend fun getWishes(): List<WishModel>
}
