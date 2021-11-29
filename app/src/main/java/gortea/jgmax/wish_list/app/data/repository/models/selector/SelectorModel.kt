package gortea.jgmax.wish_list.app.data.repository.models.selector

import gortea.jgmax.wish_list.app.data.local.entity.Selector

data class SelectorModel(
    val priceSelector: String,
    val titleSelector: String,
    val imageSelector: String
) {
    companion object {
        fun fromEntity(entity: Selector): SelectorModel {
            return SelectorModel(
                priceSelector = entity.priceSelector,
                titleSelector = entity.titleSelector,
                imageSelector = entity.imageSelector
            )
        }
    }
}
