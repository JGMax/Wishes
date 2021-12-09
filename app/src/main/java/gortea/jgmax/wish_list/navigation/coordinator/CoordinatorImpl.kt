package gortea.jgmax.wish_list.navigation.coordinator

import android.os.Bundle
import androidx.lifecycle.asFlow
import androidx.navigation.NavController
import gortea.jgmax.wish_list.R
import gortea.jgmax.wish_list.navigation.NavStorage
import gortea.jgmax.wish_list.screens.select_data_zone.SelectDataViewModel
import kotlinx.coroutines.flow.Flow

class CoordinatorImpl(
    private val navStorage: NavStorage
) : Coordinator {
    private val navController: NavController?
        get() = navStorage.navController

    override fun navigateToSelectDataZone(
        url: String,
        isLoading: Boolean,
        loadingProgress: Int
    ) {
        val args = Bundle()
        args.putString(SelectDataViewModel.URL_ARG, url)
        args.putBoolean(SelectDataViewModel.IS_LOADING_ARG, isLoading)
        args.putInt(SelectDataViewModel.LOADING_PROGRESS_ARG, loadingProgress)
        navController?.navigate(R.id.action_add_wish_to_select_data, args)
    }

    override fun navigateToWishList() {
        navController?.navigate(R.id.to_wish_list)
    }

    override fun navigateToUpdateWish(url: String?) {
        val args = url?.let {
            val bundle = Bundle()
            bundle.putString(SelectDataViewModel.URL_ARG, url)
            bundle
        }
        navController?.navigate(R.id.action_wish_list_to_add_wish, args)
    }

    override fun navigateBack() {
        navController?.popBackStack()
    }

    override fun navigateToPreferences() {
        navController?.navigate(R.id.action_wish_list_to_preference)
    }

    override fun <T> returnResult(result: T, key: String) {
        navController?.previousBackStackEntry?.savedStateHandle?.set(key, result)
    }

    override fun <T> getResult(key: String): Flow<T>? {
        return navController?.currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)?.asFlow()
    }
}
