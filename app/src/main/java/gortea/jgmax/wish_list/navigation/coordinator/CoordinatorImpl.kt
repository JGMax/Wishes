package gortea.jgmax.wish_list.navigation.coordinator

import androidx.navigation.NavController
import gortea.jgmax.wish_list.navigation.NavStorage

class CoordinatorImpl(
    private val navStorage: NavStorage
) : Coordinator {
    private val navController: NavController?
        get() = navStorage.navController
}