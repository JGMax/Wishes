package gortea.jgmax.wish_list.app.data.local.dao

import androidx.room.*
import gortea.jgmax.wish_list.app.data.local.entity.Selector

@Dao
interface SelectorsDAO {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addSelector(selector: Selector)

    @Update
    fun updateSelector(selector: Selector)

    @Query("DELETE FROM selector WHERE baseUrl LIKE :baseUrl")
    fun deleteSelector(baseUrl: String)

    @Query("SELECT * FROM selector WHERE baseUrl LIKE :baseUrl")
    fun getSelector(baseUrl: String): List<Selector>

    @Query("SELECT * FROM selector")
    fun getSelectors(): List<Selector>
}
