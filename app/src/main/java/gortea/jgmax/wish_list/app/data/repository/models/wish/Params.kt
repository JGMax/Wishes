package gortea.jgmax.wish_list.app.data.repository.models.wish

data class Params(
    val targetPrice: Long?,
    val position: Position?,
    val initialPrice: Long?,
    val notificationFrequency: Int
)
