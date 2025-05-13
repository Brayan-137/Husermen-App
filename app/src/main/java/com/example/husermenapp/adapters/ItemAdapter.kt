package com.example.husermenapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.husermenapp.FragmentUtils.applyTextViewFormat
import com.example.husermenapp.Item
import com.example.husermenapp.databinding.ItemInvetoryBinding

class ItemAdapter(
    itemList: List<Item>,
    private val handleClickItemDetails: (Item) -> Unit
) : BaseAdapter<Item, ItemInvetoryBinding>(itemList) {

    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup): ItemInvetoryBinding {
        return ItemInvetoryBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemInvetoryBinding, item: Item, position: Int) {
        binding.apply {
            tvName.text = applyTextViewFormat(item.name.toString())
            tvDescription.text = item.description
            tvPrice.text = item.price.toString()
            tvStock.text = item.stock.toString()
        }

        binding.root.setOnClickListener { handleClickItemDetails(item) }
    }
}