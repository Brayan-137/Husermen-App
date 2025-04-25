package com.example.husermenapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.husermenapp.SearchViewFragment.Companion.MODEL_REFERENCE_NAME_BUNDLE
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//        }
    }

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

//    companion object {
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            InventoryFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddItem.setOnClickListener{ handleClickBtnAddItem() }

        initSearchView(savedInstanceState)
        initRecyclerView()
    }


    private fun handleClickBtnAddItem() {
        startActivity(Intent(requireActivity(), CreateItem::class.java))
    }

    private fun initRecyclerView() {
        binding.recyclerItems.apply {
            layoutManager = LinearLayoutManager(binding.recyclerItems.context)
            adapter = ItemAdapter(emptyList())
        }

        getItems { (binding.recyclerItems.adapter as? ItemAdapter)?.updateItems(it) }
    }

    private fun getItems(callback: (List<Item>) -> Unit) {
        itemsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { it.getValue(Item::class.java) }
                println("Tamaño de la lista: ${items.size}")
                callback(items)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Error al consultar los datos.", error.toException())
            }

        })
    }

    private fun initSearchView(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) { // Se ejecuta solamente cuando la actividad es nueva
            val bundle = bundleOf(
                MODEL_REFERENCE_NAME_BUNDLE to "items"
            )
            childFragmentManager.commit {
                setReorderingAllowed(true)
                replace<SearchViewFragment>(R.id.fragmentContainerSearchView, args = bundle)
            }
        }
    }
}