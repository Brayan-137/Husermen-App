package com.example.husermenapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.husermenapp.FragmentUtils.applyTextViewFormat
import com.example.husermenapp.Item
import com.example.husermenapp.R
import com.example.husermenapp.databinding.ItemInvetoryBinding

class ItemAdapter(private var itemList: List<Item>, private var handleClickItemDetails: (Item) -> Unit): RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemLayout = LayoutInflater.from(parent.context).inflate(R.layout.item_invetory, parent, false)
        return ItemViewHolder(itemLayout)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.render(itemList[position])
    }

    fun updateItems(newItemList: List<Item>) {
        this.itemList = newItemList
        Log.d("Adapter", "El tamaño de la lista es ${this.itemList.size}")
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(itemLayout: View): RecyclerView.ViewHolder(itemLayout) {
        val binding = ItemInvetoryBinding.bind(itemLayout)

        fun render(item: Item) {
            binding.apply {
                tvName.text = applyTextViewFormat(item.name.toString())
                tvDescription.text = item.description
                tvPrice.text = item.price.toString()
                tvStock.text = item.stock.toString()
            }

            itemView.setOnClickListener{ handleClickItemDetails(item) }

            Log.d("Holder", "Funcionando ${item.name}")
        }

    }
}