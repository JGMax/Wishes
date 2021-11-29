package gortea.jgmax.wish_list.features.factory

import gortea.jgmax.wish_list.features.add_url.AddUrlFeature
import gortea.jgmax.wish_list.mvi.data.DependencyStore
import gortea.jgmax.wish_list.mvi.domain.Feature
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

class FeatureFactory @Inject constructor(
    val store: DependencyStore
) {
    inline fun <reified T : Feature<*, *, *>> createFeature(
        coroutineScope: CoroutineScope,
        type: KClass<T> = T::class
    ): T? {
        return when {
            type.isSuperclassOf(AddUrlFeature::class) -> AddUrlFeature(store, coroutineScope) as T
            else -> null
        }
    }
}
