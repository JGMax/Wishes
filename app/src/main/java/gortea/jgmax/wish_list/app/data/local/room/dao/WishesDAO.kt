package gortea.jgmax.wish_list.app.data.local.room.dao

import androidx.room.*
import gortea.jgmax.wish_list.app.data.local.room.entity.Wish

@Dao
interface WishesDAO {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addWish(wish: Wish): Long

    @Update
    fun updateWish(wish: Wish)

    @Query("DELETE FROM wish WHERE url LIKE :url")
    fun deleteWish(url: String)

    @Query("SELECT * FROM wish ORDER BY id DESC")
    fun getWishes(): List<Wish>

    @Query("SELECT * FROM wish WHERE url LIKE :url LIMIT 1")
    fun getWishesByUrl(url: String): Wish?
}
