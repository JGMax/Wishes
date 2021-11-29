package gortea.jgmax.wish_list.mvi.domain

interface Middleware<E: Event> {
    suspend fun effect(event: E): E?
}