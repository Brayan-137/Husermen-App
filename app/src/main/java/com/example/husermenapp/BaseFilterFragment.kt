package com.example.husermenapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

open class BaseFilterFragment : Fragment() {

    protected lateinit var modelRef: DatabaseReference
    protected var updateItemsRecylerView: ((newListProducts: List<Product>) -> Unit)? = null
    var isSearching: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val modelRefName = it.getString("items").toString()
            modelRef = FirebaseDatabase.getInstance().getReference(modelRefName)
        }
    }

    val setUpdateItemsRecyclerView = { updateItemsRecyclerView: (newListProducts: List<Product>) -> Unit -> this.updateItemsRecylerView = updateItemsRecyclerView }

    protected fun firebaseSearch(query: String, property: String) {
        val formatedQuery = query.lowercase()

        modelRef.orderByChild(property).startAt(formatedQuery).endAt(formatedQuery + "\uf8ff")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val filteredArray = ArrayList<Product>()

                    snapshot.children.forEach{ itemRef ->
                        val product = itemRef.getValue(Product::class.java)
                        product?.let { filteredArray.add(it) }
                    }

                    Log.d("Busqueda", "Se encontraron ${filteredArray.size} coincidencias")
                    updateItemsRecylerView?.let { it(filteredArray) }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("Firebase", "Error al consultar los datos.", error.toException())
                }
            })
    }
}