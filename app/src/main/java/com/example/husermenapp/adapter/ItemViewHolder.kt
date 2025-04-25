package com.example.husermenapp.adapter

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.husermenapp.Item
import com.example.husermenapp.databinding.ItemInvetoryBinding

class ItemViewHolder(itemLayout: View): RecyclerView.ViewHolder(itemLayout) {
    val binding = ItemInvetoryBinding.bind(itemLayout)

    fun render(item: Item) {
        binding.apply {
            tvName.text = item.name
            tvDescription.text = item.description
            tvPrice.text = item.price.toString()
            tvStock.text = item.stock.toString()
        }

        Log.d("Holder", "Funcionando ${item.name}")
    }
}