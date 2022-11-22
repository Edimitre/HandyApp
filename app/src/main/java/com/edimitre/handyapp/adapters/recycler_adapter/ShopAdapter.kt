package com.edimitre.handyapp.adapters.recycler_adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.model.Shop


class ShopAdapter(private val onShopClickListener: OnShopClickListener) :
    PagingDataAdapter<Shop, ShopAdapter.ShopViewHolder>(DiffUtilCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shop, parent, false)
        return ShopViewHolder(view)

    }


    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {

        holder.bind(getItem(position)!!, onShopClickListener)

    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Shop>() {

        override fun areItemsTheSame(oldItem: Shop, newItem: Shop): Boolean {
            Log.e(HandyAppEnvironment.TAG, "areItemsTheSame: ")
            return oldItem.shop_id == newItem.shop_id
        }

        override fun areContentsTheSame(oldItem: Shop, newItem: Shop): Boolean {
            Log.e(HandyAppEnvironment.TAG, "areContentsTheSame: ")
            return oldItem == newItem
        }

    }


    fun getSelectedItem(pos: Int): Shop? {

        return getItem(pos)
    }

    class ShopViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {


        private val shopName: TextView = itemView.findViewById(R.id.shop_name_text)


        fun bind(shop: Shop, onShopClickListener: OnShopClickListener) {


            shopName.text = shop.shop_name


            itemView.setOnClickListener {
                onShopClickListener.onShopClicked(shop)
            }

        }

    }


    interface OnShopClickListener {
        fun onShopClicked(shop: Shop)
    }


}