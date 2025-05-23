package com.example.husermenapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.husermenapp.utils.FragmentUtils.applyTextViewFormat
import com.example.husermenapp.dataclasses.MCProduct
import com.example.husermenapp.R
import com.example.husermenapp.databinding.ItemTopsellsBinding

class TSProductAdapter (private var tsProductList: List<MCProduct>, private var handleClickMCProductDetails: (MCProduct) -> Unit): RecyclerView.Adapter<TSProductAdapter.TSProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TSProductViewHolder {
        val itemLayout = LayoutInflater.from(parent.context).inflate(R.layout.item_topsells, parent, false)
        return TSProductViewHolder(itemLayout)
    }

    override fun getItemCount(): Int = tsProductList.size

    override fun onBindViewHolder(holder: TSProductViewHolder, position: Int) {
        holder.render(tsProductList[position])
    }

    fun updateItems(newItemList: List<MCProduct>) {
        this.tsProductList = newItemList
        Log.d("Adapter", "El tamaño de la lista es ${this.tsProductList.size}")
        notifyDataSetChanged()
    }

    inner class TSProductViewHolder(itemLayout: View): RecyclerView.ViewHolder(itemLayout) {
        val binding = ItemTopsellsBinding.bind(itemLayout)

        fun render(tsProduct: MCProduct) {
            binding.apply {
                tvRanking.text = tsProduct.topSells.toString()
                tvName.text = applyTextViewFormat(tsProduct.name.toString())
                tvPrice.text = tsProduct.price.toString()
                tvStock.text = tsProduct.stock.toString()
            }

            itemView.setOnClickListener{ handleClickMCProductDetails(tsProduct) }

            Log.d("Holder", "Funcionando ${tsProduct.status}")
        }

    }

}