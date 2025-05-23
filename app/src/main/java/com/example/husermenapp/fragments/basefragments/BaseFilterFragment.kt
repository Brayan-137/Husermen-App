package com.example.husermenapp.fragments.basefragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.husermenapp.dataclasses.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

open class BaseFilterFragment<T: Any>(private val modelClass: Class<T>) : Fragment() {
    protected lateinit var updateItemsRecylerView: ((newListItems: List<T>) -> Unit)
    val setUpdateItemsRecyclerView = { updateItemsRecyclerView: (newListItems: List<T>) -> Unit -> this.updateItemsRecylerView = updateItemsRecyclerView }

    protected lateinit var modelRef: DatabaseReference
    var isSearching: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val modelRefName = it.getString("items").toString()
            modelRef = FirebaseDatabase.getInstance().getReference(modelRefName)
        }
    }

    fun firebaseSearch(query: String, property: String, callback: (List<Any>) -> Unit) {
        val formatedQuery = query.lowercase()

        modelRef.orderByChild(property).startAt(formatedQuery).endAt(formatedQuery + "\uf8ff")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val filteredArray = ArrayList<T>()

                    snapshot.children.forEach{ itemRef ->
                        val item = itemRef.getValue(modelClass)
                        item?.let { filteredArray.add(it) }
                    }

                    Log.d("Busqueda", "Se encontraron ${filteredArray.size} coincidencias")
//                    updateItemsRecylerView?.let { it(filteredArray) }
                    callback(filteredArray)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("Firebase", "Error al consultar los datos.", error.toException())
                }
            })
    }
}