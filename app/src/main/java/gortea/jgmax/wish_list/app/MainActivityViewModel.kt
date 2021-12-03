package gortea.jgmax.wish_list.app

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import gortea.jgmax.wish_list.app.data.remote.loader.PageLoader
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val loader: PageLoader
) : ViewModel() {
    fun attachLoader(context: Context) {
        // Workaround to fix memory leak
        loader.attach(context)
    }

    override fun onCleared() {
        // Workaround to fix memory leak
        loader.detach()
        super.onCleared()
    }
}
