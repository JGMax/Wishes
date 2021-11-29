package gortea.jgmax.wish_list.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import gortea.jgmax.wish_list.navigation.NavStorage
import gortea.jgmax.wish_list.navigation.coordinator.Coordinator
import gortea.jgmax.wish_list.navigation.coordinator.CoordinatorImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideNavStorage(): NavStorage {
        return NavStorage()
    }

    @Provides
    fun provideCoordinator(navStorage: NavStorage): Coordinator {
        return CoordinatorImpl(navStorage)
    }
}
