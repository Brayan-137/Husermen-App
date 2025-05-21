package com.example.husermenapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.husermenapp.adapters.MCProductAdapter
import com.example.husermenapp.databinding.FragmentMercadoLibreBinding
import com.example.husermenapp.dataclasses.MCProduct
import com.example.husermenapp.api.RetrofitClient
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        getMCItems()
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
    
    private fun getMCItems() {
        val MELI_ACCESS_TOKEN = "APP_USR-8551929605562168-052117-eb1a6a50f20c989788476a9199a38379-2453143702"
        val MELI_SELLER_TEST_USER_ID = "2453143702"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val authHeader = "Bearer $MELI_ACCESS_TOKEN"

                val itemIdsResponse = RetrofitClient.mcApiService.getUserItems(
                    userId = MELI_SELLER_TEST_USER_ID,
                    authorization = authHeader,
                    offset = 0,
                    limit = 50
                )

                if (itemIdsResponse.isSuccessful && itemIdsResponse.body() != null) {
                    val itemIds = itemIdsResponse.body()!!.results

                    if (itemIds.isNotEmpty()) {
                        val itemIdsCommaSeparated = itemIds.joinToString(",")

                        val productDetailsResponse = RetrofitClient.mcApiService.getProductDetailsByIds(
                            authorization = authHeader,
                            itemIds = itemIdsCommaSeparated
                        )

                        if (productDetailsResponse.isSuccessful && productDetailsResponse.body() != null) {
                            val newProducts = productDetailsResponse.body()!!

                            Log.d("MeliApp", "Productos cargados: ${newProducts.size}")

                        } else {
                            val errorBody = productDetailsResponse.errorBody()?.string() ?: "Error desconocido"
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "Error al cargar detalles de productos: $errorBody", Toast.LENGTH_LONG).show()
                                Log.e("MeliApp", "Error al cargar detalles de productos: $errorBody")
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "El usuario no tiene publicaciones.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val errorBody = itemIdsResponse.errorBody()?.string() ?: "Error desconocido"
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error al cargar IDs de productos: $errorBody", Toast.LENGTH_LONG).show()
                        Log.e("MeliApp", "Error al cargar IDs de productos: $errorBody")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Excepción al cargar productos: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("MeliApp", "Excepción al cargar productos: ${e.message}", e)
                }
            }
        }
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