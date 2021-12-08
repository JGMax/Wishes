package gortea.jgmax.wish_list.screens.wish_list.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import gortea.jgmax.wish_list.R
import gortea.jgmax.wish_list.databinding.ItemFooterBinding
import gortea.jgmax.wish_list.databinding.ItemWishBinding
import gortea.jgmax.wish_list.screens.wish_list.list.holder.FooterViewHolder
import gortea.jgmax.wish_list.screens.wish_list.list.holder.WishViewHolder
import gortea.jgmax.wish_list.screens.wish_list.list.item.WishDataWrapper

class WishListAdapter : ListAdapter<WishDataWrapper, RecyclerView.ViewHolder>(callback) {
    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.item_footer -> {
                val binding = ItemFooterBinding.inflate(inflater, parent, false)
                FooterViewHolder(binding.root)
            }
            else -> {
                val binding = ItemWishBinding.inflate(inflater, parent, false)
                WishViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WishViewHolder -> holder.bind(getItem(position))
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            when (holder) {
                is WishViewHolder -> holder.bind(getItem(position), payloads.last())
            }
        }
    }

    fun get(position: Int): WishDataWrapper = super.getItem(position)

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

            override fun getChangePayload(
                oldItem: WishDataWrapper,
                newItem: WishDataWrapper
            ): Any {
                return oldItem.data.icon != newItem.data.icon
            }
        }
    }
}
