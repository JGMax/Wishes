package gortea.jgmax.wish_list.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import gortea.jgmax.wish_list.app.data.remote.loader.Loader
import gortea.jgmax.wish_list.app.data.remote.loader.PageLoader
import gortea.jgmax.wish_list.app.data.remote.loader.impl.LoaderImpl
import gortea.jgmax.wish_list.app.data.remote.loader.impl.PageLoaderImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoaderModule {
    @Provides
    @Singleton
    fun provideLoader(@ApplicationContext context: Context): Loader {
        return LoaderImpl(context)
    }

    @Provides
    @Singleton
    fun providePageLoader(loader: Loader): PageLoader {
        return PageLoaderImpl(loader)
    }
}