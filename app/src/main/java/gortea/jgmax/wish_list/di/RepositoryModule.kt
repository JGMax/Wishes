package gortea.jgmax.wish_list.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import gortea.jgmax.wish_list.app.data.local.dao.SelectorsDAO
import gortea.jgmax.wish_list.app.data.local.dao.WishesDAO
import gortea.jgmax.wish_list.app.data.repository.Repository
import gortea.jgmax.wish_list.app.data.repository.RepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideRepository(selectorsDAO: SelectorsDAO, wishesDAO: WishesDAO): Repository {
        return RepositoryImpl(selectorsDAO, wishesDAO)
    }
}