package gortea.jgmax.wish_list.screens.add_url.event

import gortea.jgmax.wish_list.mvi.view.ViewEvent

sealed class AddUrlViewEvent : ViewEvent {
    class AddUrl(val url: String) : AddUrlViewEvent()
}