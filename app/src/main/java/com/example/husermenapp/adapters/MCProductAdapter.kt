package com.example.husermenapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.husermenapp.fragments.FragmentUtils.applyTextViewFormat
import com.example.husermenapp.dataclasses.MCProduct
import com.example.husermenapp.R
import com.example.husermenapp.databinding.ItemMercadolibreBinding

class MCProductAdapter (private var mcProductList: List<MCProduct>, private var handleClickMCProductDetails: (MCProduct) -> Unit): RecyclerView.Adapter<MCProductAdapter.MCProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MCProductViewHolder {
        val tutorialLayout = LayoutInflater.from(parent.context).inflate(R.layout.item_mercadolibre, parent, false)
        return MCProductViewHolder(tutorialLayout)
    }

    override fun getItemCount(): Int = mcProductList.size

    override fun onBindViewHolder(holder: MCProductViewHolder, position: Int) {
        holder.render(mcProductList[position])
    }

    fun updateItems(newItemList: List<MCProduct>) {
        this.mcProductList = newItemList
        Log.d("Adapter", "El tamaño de la lista es ${this.mcProductList.size}")
        notifyDataSetChanged()
    }

    inner class MCProductViewHolder(tutorialLayout: View): RecyclerView.ViewHolder(tutorialLayout) {
        val binding = ItemMercadolibreBinding.bind(tutorialLayout)

        fun render(mcProduct: MCProduct) {
            binding.apply {
                tvName.text = applyTextViewFormat(mcProduct.name.toString())
                tvPrice.text = mcProduct.price.toString()
                tvStock.text = mcProduct.stock.toString()
                tvStatus.text = mcProduct.status
            }

            itemView.setOnClickListener{ handleClickMCProductDetails(mcProduct) }

            Log.d("Holder", "Funcionando ${mcProduct.status}")
        }

    }

}