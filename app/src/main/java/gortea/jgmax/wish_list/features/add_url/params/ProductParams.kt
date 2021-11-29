package gortea.jgmax.wish_list.features.add_url.params

data class ProductParams(
    val data: ProductParsingResult,
    val targetPrice: Long,
    val needNotify: Boolean
) {
    val currentPrice = data.currentPrice.toLong()
}