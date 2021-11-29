package gortea.jgmax.wish_list.app.data.repository

import gortea.jgmax.wish_list.app.data.repository.models.selector.SelectorModel
import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel

interface Repository {
    suspend fun getSelector(baseUrl: String): SelectorModel?
    suspend fun getSelectors(): List<SelectorModel>
    suspend fun addSelector(baseUrl: String, model: SelectorModel)
    suspend fun updateSelector(baseUrl: String, model: SelectorModel)
    suspend fun deleteSelector(baseUrl: String)

    suspend fun getWishes(): List<WishModel>
    suspend fun getWishesByGroup(group: String): List<WishModel>
    suspend fun hasWishByUrl(url: String): Boolean
    suspend fun addWish(wish: WishModel)
    suspend fun updateWish(wish: WishModel)
    suspend fun deleteWish(wish: WishModel)
}