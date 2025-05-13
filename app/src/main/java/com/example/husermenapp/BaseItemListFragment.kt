package com.example.husermenapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.husermenapp.adapters.BaseAdapter
import com.example.husermenapp.adapters.ItemAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.reflect.KMutableProperty1

abstract class BaseItemListFragment<VB: ViewBinding, T: Any, A: BaseAdapter<T, *>> : Fragment() {
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB
    protected abstract fun createAdapter(items: List<T>, handleClickItemDetails: (T) -> Unit): A

    protected abstract val items: String
    protected abstract val modelClass: Class<T>
    protected abstract val recyclerViewItems: RecyclerView
    protected abstract val tvResultsMessage: TextView

    protected val itemsRef: DatabaseReference = FirebaseDatabase.getInstance().getReference(items)
    protected lateinit var searchViewFragment: SearchViewFragment
    protected lateinit var fullItemsList: List<T>
    protected lateinit var adapter: A


    private var handleClickItemDetails: ((T) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchView(savedInstanceState)
        setupCategoryFilter(savedInstanceState)
        setupRecyclerView()
    }

    private fun updateItemsRecyclerView(newItemList: List<T>) {
        if (searchViewFragment.isSearching && newItemList.isEmpty()) {
            tvResultsMessage.visibility = View.VISIBLE
            recyclerViewItems.visibility = View.GONE
        } else {
            tvResultsMessage.visibility = View.GONE
            recyclerViewItems.visibility = View.VISIBLE

            // List will be shown if it is visible
            val tempNewItemList = newItemList.ifEmpty { fullItemsList }
            (recyclerViewItems.adapter as? BaseAdapter<T, *>)?.updateItems(tempNewItemList)
        }
    }

    open fun setupRecyclerView() {
        adapter = createAdapter(emptyList(), handleClickItemDetails!!)

        recyclerViewItems.apply {
            layoutManager = LinearLayoutManager(recyclerViewItems.context)
            adapter = this@BaseItemListFragment.adapter
        }

        getItems {
            fullItemsList = it
            updateItemsRecyclerView(fullItemsList)
        }
    }

    private fun setupCategoryFilter(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val argsCategoryFilterFragment = Bundle()
            argsCategoryFilterFragment.putString("items", items)

            val categoryFilterFragment = CategoryFilterFragment()
            categoryFilterFragment.apply {
                this.setUpdateItemsRecyclerView(::updateItemsRecyclerView)
                arguments = argsCategoryFilterFragment
            }

            childFragmentManager.beginTransaction().apply {
                setReorderingAllowed(true)
                replace(R.id.fragmentContainerFilters, categoryFilterFragment)
                commit()
            }
        }
    }

    private fun setupSearchView(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) { // Se ejecuta solamente cuando la actividad es nueva
            val argsSearchViewFragment = Bundle()
            argsSearchViewFragment.putString("items", items)

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

    private fun getItems(callback: (List<T>) -> Unit) {
        itemsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull {
                    it.getValue(modelClass)?.apply {
                        this::class.members
                            .filterIsInstance<KMutableProperty1<Any, *>>()
                            .firstOrNull { it.name == "key" }
                            ?.let { keyProperty ->
                                @Suppress("UNCHECKED_CAST")
                                (keyProperty as KMutableProperty1<Any, String?>).set(this, it.key)
                            }
                    }
                }
                callback(items)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Error al consultar los datos.", error.toException())
            }

        })
    }

    val setHandleClickItemDetails = { handleClickItemDetails: (T) -> Unit -> this.handleClickItemDetails = handleClickItemDetails }
}