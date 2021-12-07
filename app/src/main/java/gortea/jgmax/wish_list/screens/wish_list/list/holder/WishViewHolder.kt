package gortea.jgmax.wish_list.screens.wish_list.list.holder

import android.os.Build
import androidx.recyclerview.widget.RecyclerView
import gortea.jgmax.wish_list.R
import gortea.jgmax.wish_list.databinding.ItemWishBinding
import gortea.jgmax.wish_list.screens.wish_list.list.item.WishDataWrapper

class WishViewHolder(private val binding: ItemWishBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: WishDataWrapper, payload: Any? = null) {
        binding.apply {
            root.setOnClickListener { item.onClick(item.data) }
            titleTv.text = item.data.title
            targetTv.text = item.data.targetPrice
            currentPriceTv.text = item.data.currentPrice
            initialPriceTv.text = item.data.initialPrice
            changeTv.text = item.data.changeString
            val color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                root.context.getColor(item.data.changeColor)
            } else {
                root.context.resources.getColor(item.data.changeColor)
            }
            changeTv.setTextColor(color)
            percentTv.setTextColor(color)
            if (payload == null || (payload is Boolean && payload)) {
                item.data.icon?.let {
                    iconIv.setImageBitmap(it)
                } ?: iconIv.setImageResource(R.drawable.ic_shop)
            }
        }
    }
}
