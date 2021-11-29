package gortea.jgmax.wish_list.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import gortea.jgmax.wish_list.app.data.repository.models.selector.SelectorModel

@Entity
data class Selector(
    @PrimaryKey val baseUrl: String,
    val priceSelector: String,
    val titleSelector: String,
    val imageSelector: String
) {
    companion object {
        fun fromModel(baseUrl: String, model: SelectorModel): Selector {
            return Selector(
                baseUrl = baseUrl,
                priceSelector = model.priceSelector,
                titleSelector = model.titleSelector,
                imageSelector = model.imageSelector
            )
        }
    }
}