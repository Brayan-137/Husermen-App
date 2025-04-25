package com.example.husermenapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.husermenapp.SearchViewFragment.Companion.MODEL_REFERENCE_NAME_BUNDLE
import com.example.husermenapp.adapter.ItemAdapter
import com.example.husermenapp.databinding.ActivityInventoryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Inventory : AppCompatActivity() {
    private lateinit var binding: ActivityInventoryBinding
    private val itemsRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("items")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSearchView(savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.recyclerItems.apply {
            layoutManager = LinearLayoutManager(this@Inventory)
            adapter = ItemAdapter(emptyList())
        }

        getItems { (binding.recyclerItems.adapter as? ItemAdapter)?.updateItems(it) }
    }

    private fun getItems(callback: (List<Item>) -> Unit) {
        itemsRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { it.getValue(Item::class.java) }
                println("Tamaño de la lista: ${items.size}")
                callback(items)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Error al consultar los datos.", error.toException())
            }

        })
    }

    private fun initSearchView(savedInstanceState: Bundle?) {
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