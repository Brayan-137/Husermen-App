package com.example.husermenapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

open class BaseFilterFragment : Fragment() {

    private lateinit var modelRef: DatabaseReference
    protected var updateItemsRecylerView: ((newListItems: List<Item>) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val modelRefName = it.getString("items").toString()
            modelRef = FirebaseDatabase.getInstance().getReference(modelRefName)
        }
    }

    val setUpdateItemsRecyclerView = { updateItemsRecyclerView: (newListItems: List<Item>) -> Unit -> this.updateItemsRecylerView = updateItemsRecyclerView }

    protected fun firebaseSearch(query: String) {
        val formatedQuery = query.lowercase()

        modelRef.orderByChild("name").startAt(formatedQuery).endAt(formatedQuery + "\uf8ff")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val filteredArray = ArrayList<Item>()

                    snapshot.children.forEach{ itemRef ->
                        val item = itemRef.getValue(Item::class.java)
                        item?.let { filteredArray.add(it) }
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