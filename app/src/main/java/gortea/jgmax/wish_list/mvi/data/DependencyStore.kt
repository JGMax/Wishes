package gortea.jgmax.wish_list.mvi.data

import com.google.mlkit.vision.text.TextRecognizer
import gortea.jgmax.wish_list.app.data.remote.loader.PageLoader
import gortea.jgmax.wish_list.app.data.repository.Repository
import gortea.jgmax.wish_list.di.ForegroundLoader
import javax.inject.Inject

data class DependencyStore @Inject constructor(
    val repository: Repository,
    @ForegroundLoader val pageLoader: PageLoader,
    val textRecognizer: TextRecognizer
)
