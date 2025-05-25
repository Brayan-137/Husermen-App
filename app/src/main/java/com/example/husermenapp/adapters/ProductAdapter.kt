package com.example.husermenapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.husermenapp.utils.FragmentUtils.applyTextViewFormat
import com.example.husermenapp.dataclasses.Product
import com.example.husermenapp.R
import com.example.husermenapp.databinding.ItemInvetoryBinding

class ProductAdapter(private var productList: List<Product>, private var handleClickItemDetails: (Product) -> Unit): RecyclerView.Adapter<ProductAdapter.ItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemLayout = LayoutInflater.from(parent.context).inflate(R.layout.item_invetory, parent, false)
        return ItemViewHolder(itemLayout)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.render(productList[position])
    }

    fun updateItems(newProductList: List<Product>) {
        this.productList = newProductList
        Log.d("Adapter", "El tamaño de la lista es ${this.productList.size}")
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(itemLayout: View): RecyclerView.ViewHolder(itemLayout) {
        val binding = ItemInvetoryBinding.bind(itemLayout)

        fun render(product: Product) {
            binding.apply {
                tvName.text = applyTextViewFormat(product.name.toString())
                tvDescription.text = product.description
                tvCategory.text = applyTextViewFormat(product.category.toString())
                tvPrice.text = "$ " + product.price.toString()
                tvStock.text = product.stock.toString() + " Producto/s Disponible/s"
                if (product.imageUrl != null) Glide.with(itemView.context)
                    .load(product.imageUrl)
                    .transform(
                        MultiTransformation(
                            CenterCrop(),
                            RoundedCorners(16)
                        )
                    )
                    .into(binding.ivPicture)
            }

            itemView.setOnClickListener{ handleClickItemDetails(product) }

            Log.d("Holder", "Funcionando ${product.name}")
        }

    }
}