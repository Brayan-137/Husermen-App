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
import com.example.husermenapp.api.MCProductDetail
import com.example.husermenapp.databinding.FragmentMercadoLibreBinding
import com.example.husermenapp.dataclasses.MCProduct
import com.example.husermenapp.api.RetrofitClient
import com.example.husermenapp.dataclasses.Category
import com.example.husermenapp.dataclasses.Product
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
    private val categoriesRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("categories")
    //    private lateinit var searchViewFragment: SearchViewFragment
    private lateinit var fullItemsList: List<MCProduct>
    private var categoriesList: MutableList<Category> = mutableListOf()

    private val MELI_ACCESS_TOKEN = "APP_USR-8551929605562168-052214-3c90f4420e6270dcd8f5f24a5ee90e9b-2453143702"
    private val MELI_SELLER_TEST_USER_ID = "2453143702"

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

        getCategories {
            it.forEach { category -> categoriesList.add(category) }
            getMCItems()
        }
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
                            val packedProducts = productDetailsResponse.body()!!
                            val unpackedProducts = packedProducts.map { it.body }

                            Log.d("MCApp", "Productos cargados: ${unpackedProducts.size}")

                            val sellsRanking = getSellsRanking(unpackedProducts)

                            val mcProducts = unpackedProducts.map {
                                MCProduct(
                                    key = it.id,
                                    name = it.title,
                                    price = it.price.toInt(),
                                    stock = it.available_quantity,
                                    status = if (it.available_quantity > 0) "Disponible" else "Agotado",
                                    category = searchCategory(it.category_id),
                                    topSells = sellsRanking[it.title]
                                )
                            }

                            Log.d("MCApp", mcProducts.toString())

                            withContext(Dispatchers.Main) {
                                updateItemsRecyclerView(mcProducts)
                            }
                        } else {
                            val errorBody = productDetailsResponse.errorBody()?.string() ?: "Error desconocido"
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "Error al cargar detalles de productos: $errorBody", Toast.LENGTH_LONG).show()
                                Log.e("MCApp", "Error al cargar detalles de productos: $errorBody")
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
                        Log.e("MCApp", "Error al cargar IDs de productos: $errorBody")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Excepción al cargar productos: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("MCApp", "Excepción al cargar productos: ${e.message}", e)
                }
            }
        }
    }

    private fun getSellsRanking(unpackedProducts: List<MCProductDetail>): Map<String, Int> {
        val sortedBySold = unpackedProducts.map {
            mapOf(
                "name" to it.title,
                "sold" to it.price * it.sold_quantity
            )
        }
            .sortedByDescending { it["sold"] as Double }
            .mapIndexed { index, map ->
                map + ("ranking" to (index + 1))
            }

        val rankingMap = mutableMapOf<String, Int>()
        sortedBySold.forEach { map ->
            val name = map["name"] as? String ?: ""
            val ranking = map["ranking"] as? Int ?: 0
            rankingMap[name] = ranking
        }

        return rankingMap
    }

    private fun searchCategory(categoryId: String): String? {
        val searchResults = categoriesList.filter { it.id == categoryId }

        if (searchResults.isNotEmpty()) {
            return searchResults[0].name
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val authHeader = "Bearer $MELI_ACCESS_TOKEN"

                    val categoriesResponse = RetrofitClient.mcApiService.getCategoryDetailsByIds(
                        authorization = authHeader,
                        categoryId = categoryId
                    )

                    if (categoriesResponse.isSuccessful && categoriesResponse.body() != null) {
                        val categoryResponse = categoriesResponse.body()!!
                        val newCategory = Category(categoryResponse.id, categoryResponse.name)
                        withContext(Dispatchers.Main) {
                            categoriesList.add(newCategory)
                            Log.d("MCApp", newCategory.toString())
                            categoriesRef.push().setValue(newCategory)
                                .addOnSuccessListener {
                                    Log.d("Firebase", "Categoria agregada correctamente.")
                                }
                                .addOnFailureListener {
                                    Log.e("Firebase", "No se pudo agregar la cateogria.")
                                }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Excepción al cargar categorias: ${e.message}", Toast.LENGTH_LONG).show()
                        Log.e("MCApp", "Excepción al cargar categorias: ${e.message}", e)
                    }
                }
            }

        }

        return null
    }

    private fun getCategories(callback: (List<Category>) -> Unit) {
        categoriesRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = snapshot.children.mapNotNull {
                    it.getValue(Category::class.java)
                }
                callback(categories)
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