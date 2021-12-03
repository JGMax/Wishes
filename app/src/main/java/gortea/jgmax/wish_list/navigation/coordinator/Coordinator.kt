package gortea.jgmax.wish_list.navigation.coordinator

import kotlinx.coroutines.flow.Flow

interface Coordinator {
    fun navigateToSelectDataZone(
        url: String,
        isLoading: Boolean,
        loadingProgress: Int
    )

    fun navigateToWishList()

    fun navigateToUpdateWish(url: String? = null)

    fun navigateBack()

    fun <T> returnResult(result: T, key: String = "result")

    fun <T> getResult(key: String = "result"): Flow<T>?

    companion object {
        const val DETECTION_RESULT = "DETECTION_RESULT"
    }
}
