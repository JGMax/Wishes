package gortea.jgmax.wish_list.mvi.domain

fun interface DelayedEvent<E: Event> {
    fun onEvent(event: E)
}
