package gortea.jgmax.wish_list.app

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import gortea.jgmax.wish_list.app.data.remote.loader.Loader
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val loader: Loader
) : ViewModel() {
    fun attachLoader(context: Context) {
        // Workaround to fix memory leak
        if(!loader.isAttached()) {
            loader.attach(context)
        }
    }

    override fun onCleared() {
        // Workaround to fix memory leak
        loader.detach()
        super.onCleared()
    }
}