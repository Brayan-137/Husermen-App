package com.example.husermenapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.husermenapp.FragmentUtils.applyTextViewFormat
import com.example.husermenapp.R
import com.example.husermenapp.Tutorial
import com.example.husermenapp.databinding.ItemInvetoryBinding
import com.example.husermenapp.databinding.ItemTutorialBinding

class TutorialAdapter(private var tutorialList: List<Tutorial>, private var handleClickItemDetails: (Tutorial) -> Unit) :
    RecyclerView.Adapter<TutorialAdapter.TutorialViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorialViewHolder {
        val tutorialLayout = LayoutInflater.from(parent.context).inflate(R.layout.item_tutorial, parent, false)
        return TutorialViewHolder(tutorialLayout)
    }

    override fun getItemCount(): Int = tutorialList.size

    override fun onBindViewHolder(holder: TutorialViewHolder, position: Int) {
        holder.render(tutorialList[position], handleClickItemDetails)
    }

    fun updateItems(newItemList: List<Tutorial>) {
        this.tutorialList = newItemList
        Log.d("Adapter", "El tamaño de la lista es ${this.tutorialList.size}")
        notifyDataSetChanged()
    }

    inner class TutorialViewHolder(tutorialLayout: View): RecyclerView.ViewHolder(tutorialLayout) {
        val binding = ItemTutorialBinding.bind(tutorialLayout)

        fun render(tutorial: Tutorial, handleClickItemDetails: (Tutorial) -> Unit) {
            binding.apply {
                tvName.text = applyTextViewFormat(tutorial.name.toString())
                tvDescription.text = tutorial.description
                tvTopic.text = tutorial.topic.toString()
                tvType.text = tutorial.type.toString()
            }

            itemView.setOnClickListener{ handleClickItemDetails(tutorial) }

            Log.d("Holder", "Funcionando ${tutorial.name}")
        }

    }

}
