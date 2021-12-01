package gortea.jgmax.wish_list.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import gortea.jgmax.wish_list.app.data.local.dao.WishesDAO
import gortea.jgmax.wish_list.app.data.local.entity.Wish

@Database(entities = [Wish::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getWishesDao(): WishesDAO

    companion object {
        const val NAME = "WISH_LIST_DATABASE"
    }
}
