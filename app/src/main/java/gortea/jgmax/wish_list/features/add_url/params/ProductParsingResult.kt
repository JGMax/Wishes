package gortea.jgmax.wish_list.features.add_url.params

data class ProductParsingResult(
    val url: String,
    val title: String,
    val imageUrl: String,
    val currentPrice: String,
    val groupName: String
)
