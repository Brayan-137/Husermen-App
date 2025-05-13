package com.example.husermenapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<T, B : ViewBinding>(
    private var itemList: List<T>
) : RecyclerView.Adapter<BaseAdapter<T, B>.BaseViewHolder>() {

    abstract fun bind(binding: B, item: T, position: Int)

    abstract fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup): B

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = inflateBinding(LayoutInflater.from(parent.context), parent)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = itemList.size

    fun updateItems(newItemList: List<T>) {
        this.itemList = newItemList
        Log.d("Adapter", "El tamaño de la lista es ${this.itemList.size}")
        notifyDataSetChanged()
    }

    inner class BaseViewHolder(private val binding: B) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: T, position: Int) {
            this@BaseAdapter.bind(binding, item, position)
        }
    }
}
