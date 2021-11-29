package gortea.jgmax.wish_list.mvi.data

import gortea.jgmax.wish_list.app.data.remote.loader.PageLoader
import gortea.jgmax.wish_list.app.data.repository.Repository
import javax.inject.Inject

class DependencyStore @Inject constructor(
    val repository: Repository,
    val pageLoader: PageLoader
)