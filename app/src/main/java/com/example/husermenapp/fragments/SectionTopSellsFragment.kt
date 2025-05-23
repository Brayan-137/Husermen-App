package com.example.husermenapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.husermenapp.R
import com.example.husermenapp.adapters.TSProductAdapter
import com.example.husermenapp.databinding.FragmentTopSellsBinding
import com.example.husermenapp.dataclasses.MCProduct
import com.example.husermenapp.utils.MercadoLibre

class SectionTopSellsFragment : Fragment() {
    private var _binding: FragmentTopSellsBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchViewFragment: SearchViewFragment<MCProduct>
    private lateinit var fullItemsList: List<MCProduct>
    private lateinit var mercadoLibre: MercadoLibre

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
        _binding = FragmentTopSellsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mercadoLibre = MercadoLibre(requireContext())

        setupRecyclerView()
        setupSearchView(savedInstanceState)

        mercadoLibre.getCategories {
            it.forEach { category -> mercadoLibre.categoriesList.add(category) }
            mercadoLibre.getMCItems {
                updateItemsRecyclerView(it)

                fullItemsList = it
                setupMCCategoriesFilter(savedInstanceState)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerItems.apply {
            layoutManager = LinearLayoutManager(binding.recyclerItems.context)
            adapter = TSProductAdapter(emptyList(), handleClickItemDetails!!)
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
            (binding.recyclerItems.adapter as? TSProductAdapter)?.updateItems(tempNewItemList)
        }
    }

    private fun setupSearchView(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) { // Se ejecuta solamente cuando la actividad es nueva
            val argsSearchViewFragment = Bundle()

            searchViewFragment = SearchViewFragment(MCProduct::class.java, alternativeSearch = { query, callback ->
                callback(mercadoLibre.search(query, fullItemsList))
            })
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

    private fun setupMCCategoriesFilter(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {

            val tapFiltersFragment = TapFiltersFragment(MCProduct::class.java, "mcCategory", mcCategories = mercadoLibre.getCategories(fullItemsList))
            tapFiltersFragment.apply {
                setUpdateItemsRecyclerView(::updateItemsRecyclerView)
            }

            childFragmentManager.beginTransaction().apply {
                setReorderingAllowed(true)
                replace(R.id.fragmentContainerFilters, tapFiltersFragment)
                commit()
            }
        }
    }

    val setHandleClickItemDetails = { handleClickItemDetails: (MCProduct) -> Unit -> this.handleClickItemDetails = handleClickItemDetails }

}