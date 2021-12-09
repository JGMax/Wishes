package gortea.jgmax.wish_list.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import gortea.jgmax.wish_list.app.data.local.room.dao.PageDAO
import gortea.jgmax.wish_list.app.data.local.room.dao.WishesDAO
import gortea.jgmax.wish_list.app.data.local.room.database.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideWishesDao(database: AppDatabase): WishesDAO {
        return database.getWishesDao()
    }

    @Provides
    @Singleton
    fun providePageDao(database: AppDatabase): PageDAO {
        return database.getPageDao()
    }
}
