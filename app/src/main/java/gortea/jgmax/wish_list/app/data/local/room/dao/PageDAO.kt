package gortea.jgmax.wish_list.app.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import gortea.jgmax.wish_list.app.data.local.room.entity.Page

@Dao
interface PageDAO {
    @Insert
    fun add(page: Page): Long

    @Query("DELETE FROM page WHERE id = :id")
    fun delete(id: Long)

    @Query("SELECT * FROM page WHERE id = :id LIMIT 1")
    fun get(id: Long): Page?
}
