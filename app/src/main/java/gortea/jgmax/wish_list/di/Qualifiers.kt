package gortea.jgmax.wish_list.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ForegroundLoader

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BackgroundLoader
