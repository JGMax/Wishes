package gortea.jgmax.wish_list.app.data.local.dao

import androidx.room.*
import gortea.jgmax.wish_list.app.data.local.entity.Wish

@Dao
interface WishesDAO {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addWish(wish: Wish)

    @Update
    fun updateWish(wish: Wish)

    @Delete
    fun deleteWish(wish: Wish)

    @Query("SELECT * FROM wish")
    fun getWishes(): List<Wish>

    @Query("SELECT * FROM wish WHERE url LIKE :url")
    fun getWishesByUrl(url: String): List<Wish>
}
