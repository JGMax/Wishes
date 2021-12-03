package gortea.jgmax.wish_list.features.factory

import gortea.jgmax.wish_list.features.add_wish.AddWishFeature
import gortea.jgmax.wish_list.features.select_data_zone.SelectDataFeature
import gortea.jgmax.wish_list.features.wish_list.WishListFeature
import gortea.jgmax.wish_list.mvi.data.DependencyStore
import gortea.jgmax.wish_list.mvi.domain.Action
import gortea.jgmax.wish_list.mvi.domain.Event
import gortea.jgmax.wish_list.mvi.domain.Feature
import gortea.jgmax.wish_list.mvi.domain.State
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class FeatureFactory @Inject constructor(
    val store: DependencyStore
) {
    inline fun <reified S : State, reified E : Event, reified A : Action> createFeature(
        coroutineScope: CoroutineScope
    ): Feature<S, E, A>? {
        return when {
            SelectDataFeature.deals<S, E, A>() ->
                SelectDataFeature(store, coroutineScope) as Feature<S, E, A>
            AddWishFeature.deals<S, E, A>() ->
                AddWishFeature(store, coroutineScope) as Feature<S, E, A>
            WishListFeature.deals<S, E, A>() ->
                WishListFeature(store, coroutineScope) as Feature<S, E, A>
            else -> null
        }
    }
}
