package com.example.husermenapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import com.example.husermenapp.databinding.FragmentSearchViewBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchViewFragment : Fragment() {
    private var _binding: FragmentSearchViewBinding? = null
    private val binding get() = _binding!!

    private lateinit var modelRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val modelRefName = it.getString(MODEL_REFERENCE_NAME_BUNDLE).toString()
            modelRef = FirebaseDatabase.getInstance().getReference(modelRefName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchViewFragment.setOnQueryTextListener(handleQuerySearchView())
    }

    private fun handleQuerySearchView(): SearchView.OnQueryTextListener = object: SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            modelRef.orderByChild("name").startAt(query).endAt(query + "\uf8ff")
                .addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val filteredArray = ArrayList<Item>()

                        dataSnapshot.children.forEach{ snapshot ->
                            val item = snapshot.getValue(Item::class.java)
                            item?.let { filteredArray.add(it) }
                        }

                        TODO("Manejar que hacer con los datos optenidos (Mostrarlos en pantalla)")
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w("Firebase", "Error al consultar los datos.", databaseError.toException())
                    }
                })
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return true
        }
    }


    companion object {
        const val MODEL_REFERENCE_NAME_BUNDLE = "items"

//        @JvmStatic
//        fun newInstance(param2: String) =
//            SearchViewFragment().apply {
//                arguments = Bundle().apply {
//                    putString(FILTER_LIST_BUNDLE, param2)
//                }
//            }
    }
}