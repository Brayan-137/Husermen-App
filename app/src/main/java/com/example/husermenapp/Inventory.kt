package com.example.husermenapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.husermenapp.SearchViewFragment.Companion.MODEL_REFERENCE_NAME_BUNDLE
import com.example.husermenapp.databinding.ActivityInventoryBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Inventory : AppCompatActivity() {
    private lateinit var binding: ActivityInventoryBinding
    private val itemsRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("items")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSearchView(savedInstanceState)
    }

    private fun setupSearchView(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) { // Se ejecuta solamente cuando la actividad es nueva
            val bundle = bundleOf(
                MODEL_REFERENCE_NAME_BUNDLE to "items"
            )
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<SearchViewFragment>(R.id.fragmentContainerSearchView, args = bundle)
            }
        }
    }
}