package com.example.husermenapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.husermenapp.R
import com.example.husermenapp.TutorialActivity
import com.example.husermenapp.adapters.TutorialAdapter
import com.example.husermenapp.databinding.FragmentTutorialsBinding
import com.example.husermenapp.dataclasses.Tutorial
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SectionTutorialsFragment : Fragment() {
    private var _binding: FragmentTutorialsBinding? = null
    private val binding get() = _binding!!

    private val modelRef: String = "tutorials"
    private val tutorialsRef: DatabaseReference = FirebaseDatabase.getInstance().getReference(modelRef)
    private lateinit var searchViewFragment: SearchViewFragment<Tutorial>
    private lateinit var fullItemsList: List<Tutorial>

    private var handleClickItemDetails: ((Tutorial) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTutorialsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddItem.setOnClickListener{ handleClickBtnAddTutorial() }

        setupRecyclerView()
        setupSearchView(savedInstanceState)
        setupTopicFilter(savedInstanceState)
    }

    private fun handleClickBtnAddTutorial() {
        val createTutorialIntent = Intent(requireActivity(), TutorialActivity::class.java)
        val newTutorialRef = tutorialsRef.push()
        val newTutorial = Tutorial(newTutorialRef.key.toString())
        createTutorialIntent.putExtra("selectedTutorial", newTutorial)
        createTutorialIntent.putExtra("isCreatingNewTutorial", true)
        startActivity(createTutorialIntent)
    }

    private fun setupRecyclerView() {
        binding.recyclerItems.apply {
            layoutManager = LinearLayoutManager(binding.recyclerItems.context)
            adapter = TutorialAdapter(emptyList(), handleClickItemDetails!!)
        }

        getItems {
            fullItemsList = it
            updateItemsRecyclerView(fullItemsList)
        }
    }

    private fun updateItemsRecyclerView(newItemList: List<Tutorial>) {
        if (searchViewFragment.isSearching && newItemList.isEmpty()) {
            binding.tvResultsMessage.visibility = View.VISIBLE
            binding.recyclerItems.visibility = View.GONE
        } else {
            binding.tvResultsMessage.visibility = View.GONE
            binding.recyclerItems.visibility = View.VISIBLE

            // List will be shown if it is visible
            val tempNewItemList = newItemList.ifEmpty { fullItemsList }
            (binding.recyclerItems.adapter as? TutorialAdapter)?.updateItems(tempNewItemList)
        }
    }

    private fun getItems(callback: (List<Tutorial>) -> Unit) {
        tutorialsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tutorials = snapshot.children.mapNotNull {
                    it.getValue(Tutorial::class.java)?.apply { key = it.key }
                }
                callback(tutorials)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Error al consultar los datos.", error.toException())
            }

        })
    }

    private fun setupSearchView(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) { // Se ejecuta solamente cuando la actividad es nueva
            val argsSearchViewFragment = Bundle()
            argsSearchViewFragment.putString("items", modelRef)

            searchViewFragment = SearchViewFragment(Tutorial::class.java)
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

    private fun setupTopicFilter(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val argsTapFilterFragment = Bundle()
            argsTapFilterFragment.putString("items", modelRef)

            val tapFiltersFragment = TapFiltersFragment(Tutorial::class.java, "topic")
            tapFiltersFragment.apply {
                setUpdateItemsRecyclerView(::updateItemsRecyclerView)
                arguments = argsTapFilterFragment
            }

            childFragmentManager.beginTransaction().apply {
                setReorderingAllowed(true)
                replace(R.id.fragmentContainerFilters, tapFiltersFragment)
                commit()
            }
        }
    }

    val setHandleClickItemDetails = { handleClickItemDetails: (Tutorial) -> Unit -> this.handleClickItemDetails = handleClickItemDetails }

}