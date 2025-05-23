package com.example.husermenapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.husermenapp.databinding.FragmentCategoryFilterBinding
import com.example.husermenapp.dataclasses.Product
import com.example.husermenapp.dataclasses.Tutorial
import com.example.husermenapp.utils.FragmentUtils.applyTextViewFormat
import com.example.husermenapp.fragments.basefragments.BaseFilterFragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class TapFiltersFragment<T: Any>(
    private val modelClass: Class<T>,
    private val property: String,
    private val statuses: Set<String>? = null,
    private val mcCategories: Set<String>? = null
) : BaseFilterFragment<T>(modelClass) {
    private var _binding: FragmentCategoryFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if (_binding == null) return

        when (property) {
            "category" -> getCategories { setupTabLayout(it) }
            "topic" -> getTopics { setupTabLayout(it) }
            "status" -> if (statuses != null) setupTabLayout(statuses)
            "mcCategory" -> if (mcCategories != null) setupTabLayout(mcCategories)
        }
    }

    private fun setupTabLayout(tapOptions: Set<String>) {
        tapOptions.forEach { it1 -> binding.tabLayoutCategory.addTab(binding.tabLayoutCategory.newTab().setText(it1)) }

        binding.tabLayoutCategory.addOnTabSelectedListener(object:
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                isSearching = true
                firebaseSearch(tab?.text.toString(), property) {
                    updateItemsRecylerView(it as List<T>)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                isSearching = false
                updateItemsRecylerView?.let { it1 -> it1(listOf()) }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                if (isSearching) onTabUnselected(tab) else onTabSelected(tab)
            }
        })
    }

    private fun getCategories(callback: (Set<String>) -> Unit) {
        modelRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = mutableSetOf<String>()
                snapshot.children.forEach {
                    it.getValue(Product::class.java)?.category?.let { it1 ->
                        applyTextViewFormat(categories.add(it1).toString())
                    }
                }
                callback(categories)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Error al consultar los datos.", error.toException())
            }

        })
    }

    private fun getTopics(callback: (Set<String>) -> Unit) {
        modelRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val topics = mutableSetOf<String>()
                snapshot.children.forEach {
                    it.getValue(Tutorial::class.java)?.topic?.let { it1 ->
                        applyTextViewFormat(topics.add(it1).toString())
                    }
                }
                callback(topics)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Error al consultar los datos.", error.toException())
            }

        })
    }
}