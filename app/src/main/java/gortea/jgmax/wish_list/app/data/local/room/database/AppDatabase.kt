package gortea.jgmax.wish_list.app.data.local.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import gortea.jgmax.wish_list.app.data.local.room.converters.BitmapConverter
import gortea.jgmax.wish_list.app.data.local.room.dao.PageDAO
import gortea.jgmax.wish_list.app.data.local.room.dao.WishesDAO
import gortea.jgmax.wish_list.app.data.local.room.entity.Page
import gortea.jgmax.wish_list.app.data.local.room.entity.Wish

@Database(entities = [Wish::class, Page::class], version = 1)
@TypeConverters(BitmapConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getWishesDao(): WishesDAO
    abstract fun getPageDao(): PageDAO

    companion object {
        const val NAME = "WISH_LIST_DATABASE"
    }
}
