package com.example.husermenapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.husermenapp.Item
import com.example.husermenapp.R

class ItemAdapter (private var itemList: List<Item>): RecyclerView.Adapter<ItemViewHolder>() {
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
}