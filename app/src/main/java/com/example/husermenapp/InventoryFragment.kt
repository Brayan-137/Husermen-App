package com.example.husermenapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.husermenapp.adapter.ItemAdapter
import com.example.husermenapp.databinding.FragmentInventoryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class InventoryFragment : Fragment() {
    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!

    private val itemsRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("items")
    private lateinit var searchViewFragment: SearchViewFragment
    private lateinit var fullItemsList: List<Item>

    private var handleClickItemDetails: ((Item) -> Unit)? = null

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddItem.setOnClickListener{ handleClickBtnAddItem() }

        initSearchView(savedInstanceState)
        initRecyclerView()

    }


    private fun handleClickBtnAddItem() {
        startActivity(Intent(requireActivity(), ProductActivity::class.java))
    }

    private fun initRecyclerView() {
        binding.recyclerItems.apply {
            layoutManager = LinearLayoutManager(binding.recyclerItems.context)
            adapter = ItemAdapter(emptyList(), handleClickItemDetails!!)
        }

        getItems {
            fullItemsList = it
            updateItemsRecyclerView(fullItemsList)
        }
    }

    private fun updateItemsRecyclerView(newItemList: List<Item>) {
        Log.d("RecyclerView", "Actualizando lista...")
        if (searchViewFragment.isSearching && newItemList.isEmpty()) {
            binding.tvResultsMessage.visibility = View.VISIBLE
            binding.recyclerItems.visibility = View.GONE
        } else {
            binding.tvResultsMessage.visibility = View.GONE
            binding.recyclerItems.visibility = View.VISIBLE

            // Solo se mostrará la lista en caso de que esté visible
            val tempNewItemList = newItemList.ifEmpty { fullItemsList }
            (binding.recyclerItems.adapter as? ItemAdapter)?.updateItems(tempNewItemList)
        }
    }

    private fun getItems(callback: (List<Item>) -> Unit) {
        itemsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { it.getValue(Item::class.java) }
                callback(items)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Error al consultar los datos.", error.toException())
            }

        })
    }

    private fun initSearchView(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) { // Se ejecuta solamente cuando la actividad es nueva
            val argsSearchViewFragment = Bundle()
            argsSearchViewFragment.putString("items", "items")

            searchViewFragment = SearchViewFragment()
            searchViewFragment.apply {
                setUpdateItemsRecyclerView(::updateItemsRecyclerView)
                arguments = argsSearchViewFragment
            }

            childFragmentManager.beginTransaction().apply {
                setReorderingAllowed(true)
                replace(R.id.fragmentContainerSearchView, searchViewFragment)
                commit()
            }
        }
    }

    val setHandleClickItemDetails = { handleClickItemDetails: (Item) -> Unit -> this.handleClickItemDetails = handleClickItemDetails }
}