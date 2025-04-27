package com.example.husermenapp.adapter

import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.husermenapp.Item
import com.example.husermenapp.databinding.ItemInvetoryBinding

class ItemViewHolder(itemLayout: View): RecyclerView.ViewHolder(itemLayout) {
    val binding = ItemInvetoryBinding.bind(itemLayout)

    fun render(item: Item, handleClickItemDetails: (Item) -> Unit) {
        binding.apply {
            tvName.text = item.name?.replaceFirstChar { it.uppercaseChar() }
            tvDescription.text = item.description
            tvPrice.text = item.price.toString()
            tvStock.text = item.stock.toString()
        }

        itemView.setOnClickListener{ handleClickItemDetails(item) }

        Log.d("Holder", "Funcionando ${item.name}")
    }

}