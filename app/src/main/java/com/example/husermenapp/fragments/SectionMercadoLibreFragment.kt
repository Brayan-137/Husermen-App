package com.example.husermenapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.husermenapp.adapters.MCProductAdapter
import com.example.husermenapp.databinding.FragmentMercadoLibreBinding
import com.example.husermenapp.dataclasses.MCProduct
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SectionMercadoLibreFragment : Fragment() {
    private var _binding: FragmentMercadoLibreBinding? = null
    private val binding get() = _binding!!

    private val modelRef: String = "mcProducts"
    private val mercadoLibreProductsRef: DatabaseReference = FirebaseDatabase.getInstance().getReference(modelRef)
    //    private lateinit var searchViewFragment: SearchViewFragment
    private lateinit var fullItemsList: List<MCProduct>

    private var handleClickItemDetails: ((MCProduct) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMercadoLibreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
//        setupSearchView(savedInstanceState)
//        setupTopicFilter(savedInstanceState)
    }

    private fun setupRecyclerView() {
        binding.recyclerItems.apply {
            layoutManager = LinearLayoutManager(binding.recyclerItems.context)
            adapter = MCProductAdapter(emptyList(), handleClickItemDetails!!)
        }

        getItems {
            fullItemsList = it
            updateItemsRecyclerView(fullItemsList)
        }
    }

    private fun updateItemsRecyclerView(newItemList: List<MCProduct>) {
//        if (searchViewFragment.isSearching && newItemList.isEmpty()) { TODO: Reemplazar la línea de abajo por esta cuando se inicialice el searchView
        if (newItemList.isEmpty()) {
            binding.tvResultsMessage.visibility = View.VISIBLE
            binding.recyclerItems.visibility = View.GONE
        } else {
            binding.tvResultsMessage.visibility = View.GONE
            binding.recyclerItems.visibility = View.VISIBLE

            // List will be shown if it is visible
            val tempNewItemList = newItemList.ifEmpty { fullItemsList }
            (binding.recyclerItems.adapter as? MCProductAdapter)?.updateItems(tempNewItemList)
        }
    }

    private fun getItems(callback: (List<MCProduct>) -> Unit) {
         mercadoLibreProductsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mercadoLibreProducts = snapshot.children.mapNotNull {
                    it.getValue(MCProduct::class.java)?.apply { key = it.key }
                }
                callback(mercadoLibreProducts)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Error al consultar los datos.", error.toException())
            }

        })
    }

//    private fun setupSearchView(savedInstanceState: Bundle?) {
//        if (savedInstanceState == null) { // Se ejecuta solamente cuando la actividad es nueva
//            val argsSearchViewFragment = Bundle()
//            argsSearchViewFragment.putString("items", modelRef)
//
//            searchViewFragment = SearchViewFragment()
//            searchViewFragment.apply {
//                setUpdateItemsRecyclerView(::updateItemsRecyclerView)
//                arguments = argsSearchViewFragment
//            }
//
//            childFragmentManager.beginTransaction().apply {
//                setReorderingAllowed(true)
//                replace(R.id.fragmentContainerSearchView, searchViewFragment)
//                commit()
//            }
//        }
//    }

    val setHandleClickItemDetails = { handleClickItemDetails: (MCProduct) -> Unit -> this.handleClickItemDetails = handleClickItemDetails }

}