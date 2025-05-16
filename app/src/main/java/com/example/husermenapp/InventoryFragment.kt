package com.example.husermenapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
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

    private val modelRef: String = "items"
    private val productsRef: DatabaseReference = FirebaseDatabase.getInstance().getReference(modelRef)
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

        binding.btnAddItem.setOnClickListener{ handleClickBtnAddProduct() }

        setupRecyclerView()
        setupSearchView(savedInstanceState)
        setupCategoryFilter(savedInstanceState)

        // Going to previous activity when back is pressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStack()
            } else {
                requireActivity().finish()
            }
        }
    }

    private fun setupCategoryFilter(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val argsCategoryFilterFragment = Bundle()
            argsCategoryFilterFragment.putString("items", modelRef)

            val categoryFilterFragment = CategoryFilterFragment()
            categoryFilterFragment.apply {
                setUpdateItemsRecyclerView(::updateItemsRecyclerView)
                arguments = argsCategoryFilterFragment
            }

            childFragmentManager.beginTransaction().apply {
                setReorderingAllowed(true)
                replace(R.id.fragmentContainerFilters, categoryFilterFragment)
                commit()
            }
        }
    }

    private fun handleClickBtnAddProduct() {
        val createProductIntent = Intent(requireActivity(), ProductActivity::class.java)
        val newProductRef = productsRef.push()
        val newProduct = Item(newProductRef.key.toString())
        createProductIntent.putExtra("selectedProduct", newProduct)
        createProductIntent.putExtra("isCreatingNewProduct", true)
        startActivity(createProductIntent)
    }

    private fun setupRecyclerView() {
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
        if (searchViewFragment.isSearching && newItemList.isEmpty()) {
            binding.tvResultsMessage.visibility = View.VISIBLE
            binding.recyclerItems.visibility = View.GONE
        } else {
            binding.tvResultsMessage.visibility = View.GONE
            binding.recyclerItems.visibility = View.VISIBLE

            // List will be shown if it is visible
            val tempNewItemList = newItemList.ifEmpty { fullItemsList }
            (binding.recyclerItems.adapter as? ItemAdapter)?.updateItems(tempNewItemList)
        }
    }

    private fun getItems(callback: (List<Item>) -> Unit) {
        productsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull {
                    it.getValue(Item::class.java)?.apply { key = it.key }
                }
                callback(items)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Error al consultar los datos.", error.toException())
            }

        })
    }

    private fun setupSearchView(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) { // Se ejecuta solamente cuando la actividad es nueva
            val argsSearchViewFragment = Bundle()
            argsSearchViewFragment.putString("items", modelRef)

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