package gortea.jgmax.wish_list.app.data.repository.models.wish

data class Params(
    val url: String,
    val targetPrice: Long,
    val notificationFrequency: Int
)