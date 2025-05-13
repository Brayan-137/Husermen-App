package com.example.husermenapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.husermenapp.adapters.ItemAdapter
import com.example.husermenapp.databinding.FragmentTutorialsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class TutorialsFragment : BaseItemListFragment<FragmentTutorialsBinding, Tutorial>() {

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentTutorialsBinding.inflate(inflater, container, false)

    override val items: String
        get() = "tutorials"

    override val recyclerViewItems: RecyclerView
        get() = binding.recyclerViewItems

    override val tvResultsMessage: TextView
        get() = binding.tvResultsMessage

}