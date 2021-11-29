package gortea.jgmax.wish_list.app.data.repository

import gortea.jgmax.wish_list.app.data.local.dao.SelectorsDAO
import gortea.jgmax.wish_list.app.data.local.dao.WishesDAO
import gortea.jgmax.wish_list.app.data.local.entity.Selector
import gortea.jgmax.wish_list.app.data.local.entity.Wish
import gortea.jgmax.wish_list.app.data.repository.models.selector.SelectorModel
import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepositoryImpl(
    private val selectorsDAO: SelectorsDAO,
    private val wishesDAO: WishesDAO
) : Repository {
    override suspend fun getSelector(baseUrl: String): SelectorModel? {
        return withContext(Dispatchers.IO) {
            selectorsDAO
                .getSelector(baseUrl)
                .firstOrNull()
                ?.let { SelectorModel.fromEntity(it) }
        }
    }

    override suspend fun getSelectors(): List<SelectorModel> {
        return withContext(Dispatchers.IO) {
            selectorsDAO
                .getSelectors()
                .map { SelectorModel.fromEntity(it) }
        }
    }

    override suspend fun addSelector(baseUrl: String, model: SelectorModel) {
        withContext(Dispatchers.IO) {
            selectorsDAO.addSelector(Selector.fromModel(baseUrl, model))
        }
    }

    override suspend fun updateSelector(baseUrl: String, model: SelectorModel) {
        withContext(Dispatchers.IO) {
            selectorsDAO.updateSelector(Selector.fromModel(baseUrl, model))
        }
    }

    override suspend fun deleteSelector(baseUrl: String) {
        withContext(Dispatchers.IO) {
            selectorsDAO.deleteSelector(baseUrl)
        }
    }

    override suspend fun getWishes(): List<WishModel> {
        return withContext(Dispatchers.IO) {
            wishesDAO.getWishes().map { WishModel.fromEntity(it) }
        }
    }

    override suspend fun getWishesByGroup(group: String): List<WishModel> {
        return withContext(Dispatchers.IO) {
            wishesDAO.getWishesByGroup(group).map { WishModel.fromEntity(it) }
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