package com.example.husermenapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import com.example.husermenapp.databinding.FragmentSearchViewBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchViewFragment : BaseFilterFragment() {
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

    private fun handleQuerySearchView(): SearchView.OnQueryTextListener = object: SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            firebaseSearch(query, property)
            binding.searchViewFragment.clearFocus()
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            if (newText?.isEmpty() == true) {
                isSearching = false
                updateItemsRecylerView?.let { it(listOf()) }
            } else {
                isSearching = true
                firebaseSearch(newText!!, property)
            }

            return true
        }
    }
}