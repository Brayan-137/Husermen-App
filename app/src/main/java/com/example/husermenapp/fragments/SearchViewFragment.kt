package com.example.husermenapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import com.example.husermenapp.databinding.FragmentSearchViewBinding
import com.example.husermenapp.dataclasses.Product
import com.example.husermenapp.fragments.basefragments.BaseFilterFragment
import com.example.husermenapp.utils.MercadoLibre

class SearchViewFragment<T: Any>(
    private val modelClass: Class<T>,
    private val alternativeSearch: ((query: String, callback: (List<T>) -> Unit) -> Unit)? = null
) : BaseFilterFragment<T>(modelClass) {
    private var _binding: FragmentSearchViewBinding? = null
    private val binding get() = _binding!!
    private val property = "name"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchViewFragment.setOnQueryTextListener(handleQuerySearchView())
    }

    private fun handleQuerySearchView(): SearchView.OnQueryTextListener = object:
        SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            if (alternativeSearch == null) {
                firebaseSearch(query, property) {
                    updateItemsRecylerView(it as List<T>)
                }
            } else {
                alternativeSearch?.let { it1 ->
                    it1(query) {
                        updateItemsRecylerView(it)
                    }
                }
            }

            binding.searchViewFragment.clearFocus()
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            if (newText?.isEmpty() == true) {
                isSearching = false
                updateItemsRecylerView?.let { it(listOf()) }
            } else {
                isSearching = true
                newText?.let {
                    if (alternativeSearch == null) {
                        firebaseSearch(newText, property) {
                            updateItemsRecylerView(it as List<T>)
                        }
                    } else {
                        alternativeSearch?.let { it1 ->
                            it1(newText) {
                                updateItemsRecylerView(it)
                            }
                        }
//                        updateItemsRecylerView(alternativeSearch?.let { it(newText) } as List<T>)
                    }
                }

            }

            return true
        }
    }
}