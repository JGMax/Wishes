package gortea.jgmax.wish_list.app.data.repository

import gortea.jgmax.wish_list.app.data.local.room.dao.WishesDAO
import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RepositoryImpl(
    private val wishesDAO: WishesDAO
) : Repository {
    override suspend fun getWishesFlow(): Flow<List<WishModel>> {
        return withContext(Dispatchers.IO) {
            wishesDAO.getWishesFlow().map { list -> list.map { WishModel.fromEntity(it) } }
        }
    }

    override suspend fun hasWishByUrl(url: String): Boolean {
        return withContext(Dispatchers.IO) {
            wishesDAO.getWishByUrl(url) != null
        }
    }

    override suspend fun getWish(url: String): WishModel? {
        return withContext(Dispatchers.IO) {
            wishesDAO.getWishByUrl(url)?.let { WishModel.fromEntity(it) }
        }
    }

    override suspend fun addWish(wish: WishModel): Long {
        return withContext(Dispatchers.IO) {
            wishesDAO.addWish(wish.toEntity())
        }
    }

    override suspend fun updateWish(wish: WishModel) {
        withContext(Dispatchers.IO) {
            wishesDAO.updateWish(wish.toEntity())
        }
    }

    override suspend fun deleteWish(url: String) {
        withContext(Dispatchers.IO) {
            wishesDAO.deleteWish(url)
        }
    }

    override suspend fun getWishes(): List<WishModel> {
        return withContext(Dispatchers.IO) {
            wishesDAO.getWishes().map { WishModel.fromEntity(it) }
        }
    }
}
