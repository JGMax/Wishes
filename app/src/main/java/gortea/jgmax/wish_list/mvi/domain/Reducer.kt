package gortea.jgmax.wish_list.mvi.domain

interface Reducer<S: State, E: Event, A: Action> {
    fun reduce(event: E, state: S): Pair<S?, A?>
}