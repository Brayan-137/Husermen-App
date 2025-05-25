package com.example.husermenapp.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.husermenapp.api.MCProductDetail
import com.example.husermenapp.api.RetrofitClient
import com.example.husermenapp.dataclasses.Category
import com.example.husermenapp.dataclasses.MCProduct
import com.example.husermenapp.utils.FragmentUtils.applyTextViewFormat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MercadoLibre(private var context: Context) {
    private val MELI_ACCESS_TOKEN = "APP_USR-8551929605562168-052307-f817c4ecbbf7fa1c66e7b286a16bd398-2453143702"
    private val MELI_SELLER_TEST_USER_ID = "2453143702"

    private val categoriesRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("categories")
    var categoriesList: MutableList<Category> = mutableListOf()

    init {

    }

    fun getMCItems(callback: (List<MCProduct>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val authHeader = "Bearer $MELI_ACCESS_TOKEN"

                val itemIdsResponse = RetrofitClient().mcApiService.getUserItems(
                    userId = MELI_SELLER_TEST_USER_ID,
                    authorization = authHeader,
                    offset = 0,
                    limit = 50
                )

                if (itemIdsResponse.isSuccessful && itemIdsResponse.body() != null) {
                    val itemIds = itemIdsResponse.body()!!.results

                    if (itemIds.isNotEmpty()) {
                        val itemIdsCommaSeparated = itemIds.joinToString(",")

                        val productDetailsResponse = RetrofitClient().mcApiService.getProductDetailsByIds(
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
                                    id = it.id,
                                    name = it.title,
                                    price = it.price.toInt(),
                                    stock = it.available_quantity,
                                    status = if (it.available_quantity > 0) "Disponible" else "Agotado",
                                    category = searchCategory(it.category_id),
                                    topSells = sellsRanking[it.title],
                                    imageUrl = it.thumbnail
                                )
                            }

                            Log.d("MCApp", mcProducts.toString())

                            withContext(Dispatchers.Main) {
                                callback(mcProducts)
                            }
                        } else {
                            val errorBody = productDetailsResponse.errorBody()?.string() ?: "Error desconocido"
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Error al cargar detalles de productos: $errorBody", Toast.LENGTH_LONG).show()
                                Log.e("MCApp", "Error al cargar detalles de productos: $errorBody")
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "El usuario no tiene publicaciones.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val errorBody = itemIdsResponse.errorBody()?.string() ?: "Error desconocido"
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error al cargar IDs de productos: $errorBody", Toast.LENGTH_LONG).show()
                        Log.e("MCApp", "Error al cargar IDs de productos: $errorBody")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Excepción al cargar productos: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("MCApp", "Excepción al cargar productos: ${e.message}", e)
                }
            }
        }
    }

    fun getSellsRanking(unpackedProducts: List<MCProductDetail>): Map<String, Int> {
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

                    val categoriesResponse = RetrofitClient().mcApiService.getCategoryDetailsByIds(
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
                        Toast.makeText(context, "Excepción al cargar categorias: ${e.message}", Toast.LENGTH_LONG).show()
                        Log.e("MCApp", "Excepción al cargar categorias: ${e.message}", e)
                    }
                }
            }

        }

        return null
    }

    fun getCategories(callback: (List<Category>) -> Unit) {
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

    fun fetchSalesData(callback: (Map<String, Int>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient().mcApiService.getOrders(
                    sellerId = MELI_SELLER_TEST_USER_ID,
                    token = "Bearer $MELI_ACCESS_TOKEN"
                )

                val formatterInput = DateTimeFormatter.ISO_OFFSET_DATE_TIME
                val formatterOutput = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                val salesData = response.results
                    .map {
                        LocalDate.parse(it.dateCreated, formatterInput).format(formatterOutput)
                    }
                    .groupingBy { it }
                    .eachCount()
                    .toSortedMap()

                withContext(Dispatchers.Main) {
                    callback(salesData)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback(emptyMap())
                    Log.e("FetchError", "Error fetching sales data: ${e.message}")
                }
            }
        }
    }

    fun getStatuses(mcProductList: List<MCProduct>): Set<String> {
        val status = mutableSetOf<String>()
        mcProductList.forEach{ applyTextViewFormat(it.status?.let { it1 -> status.add(it1) }.toString()) }
        return status
    }

    fun getCategories(mcProductList: List<MCProduct>): Set<String> {
        val categories = mutableSetOf<String>()
        mcProductList.forEach{ applyTextViewFormat(it.category?.let { it1 -> categories.add(it1) }.toString()) }
        return categories
    }

    fun search(query: String, mcProductList: List<MCProduct>): List<MCProduct> {
        Log.d("MercadoLibre", "$query $mcProductList")
        return mcProductList.filter { it.name?.lowercase()?.startsWith(query.lowercase()) ?: false }
    }
}