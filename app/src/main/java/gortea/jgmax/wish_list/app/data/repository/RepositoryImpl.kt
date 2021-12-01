package gortea.jgmax.wish_list.app.data.repository

import gortea.jgmax.wish_list.app.data.local.dao.WishesDAO
import gortea.jgmax.wish_list.app.data.local.entity.Wish
import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepositoryImpl(
    private val wishesDAO: WishesDAO
) : Repository {
    override suspend fun getWishes(): List<WishModel> {
        return withContext(Dispatchers.IO) {
            wishesDAO.getWishes().map { WishModel.fromEntity(it) }
        }
    }

    override suspend fun hasWishByUrl(url: String): Boolean {
        return withContext(Dispatchers.IO) {
            wishesDAO.getWishesByUrl(url).isNotEmpty()
        }
    }

    override suspend fun addWish(wish: WishModel) {
        withContext(Dispatchers.IO) {
            wishesDAO.addWish(Wish.fromModel(wish))
        }
    }

    override suspend fun updateWish(wish: WishModel) {
        withContext(Dispatchers.IO) {
            wishesDAO.updateWish(Wish.fromModel(wish))
        }
    }

    override suspend fun deleteWish(wish: WishModel) {
        withContext(Dispatchers.IO) {
            wishesDAO.deleteWish(Wish.fromModel(wish))
        }
    }
}