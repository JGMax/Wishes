package gortea.jgmax.wish_list.screens.wish_list.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import gortea.jgmax.wish_list.databinding.ItemWishBinding
import gortea.jgmax.wish_list.screens.wish_list.list.holder.WishViewHolder
import gortea.jgmax.wish_list.screens.wish_list.list.item.WishDataWrapper

class WishListAdapter : ListAdapter<WishDataWrapper, WishViewHolder>(callback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishViewHolder {
        val binding = ItemWishBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WishViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WishViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private companion object {
        private val callback = object : DiffUtil.ItemCallback<WishDataWrapper>() {
            override fun areItemsTheSame(
                oldItem: WishDataWrapper,
                newItem: WishDataWrapper
            ): Boolean {
                return oldItem.data.url == newItem.data.url
            }

            override fun areContentsTheSame(
                oldItem: WishDataWrapper,
                newItem: WishDataWrapper
            ): Boolean {
                return oldItem.data == newItem.data
            }
        }
    }
}
