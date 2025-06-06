package com.example.husermenapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.husermenapp.utils.FragmentUtils.applyTextViewFormat
import com.example.husermenapp.dataclasses.MCProduct
import com.example.husermenapp.R
import com.example.husermenapp.databinding.ItemMercadolibreBinding

class MCProductAdapter (private var mcProductList: List<MCProduct>, private var handleClickMCProductDetails: (MCProduct) -> Unit): RecyclerView.Adapter<MCProductAdapter.MCProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MCProductViewHolder {
        val itemLayout = LayoutInflater.from(parent.context).inflate(R.layout.item_mercadolibre, parent, false)
        return MCProductViewHolder(itemLayout)
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

    inner class MCProductViewHolder(itemLayout: View): RecyclerView.ViewHolder(itemLayout) {
        val binding = ItemMercadolibreBinding.bind(itemLayout)

        fun render(mcProduct: MCProduct) {
            binding.apply {
                tvName.text = applyTextViewFormat(mcProduct.name.toString())
                tvPrice.text = "$ " + mcProduct.price.toString()
                tvStock.text = mcProduct.stock.toString() + " Producto/s Diponible/s"
                tvStatus.text = mcProduct.status
                if (mcProduct.imageUrl != null) Glide.with(itemView.context)
                    .load(mcProduct.imageUrl)
                    .transform(
                        MultiTransformation(
                            CenterCrop(),
                            RoundedCorners(16)
                        )
                    )
                    .into(binding.ivPicture)
            }

            itemView.setOnClickListener{ handleClickMCProductDetails(mcProduct) }

            Log.d("Holder", "Funcionando ${mcProduct.status}")
        }

    }

}