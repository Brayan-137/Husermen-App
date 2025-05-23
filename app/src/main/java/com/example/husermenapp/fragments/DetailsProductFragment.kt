package com.example.husermenapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.husermenapp.R
import com.example.husermenapp.databinding.FragmentProductDetailsBinding
import com.example.husermenapp.dataclasses.Product
import com.example.husermenapp.utils.FragmentUtils.applyTextViewFormat
import com.example.husermenapp.utils.FragmentUtils.replaceFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DetailsProductFragment : Fragment() {
    private var _binding: FragmentProductDetailsBinding? = null
    val binding get() = _binding!!

    private val productsRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("items")
    private var product: Product? = null
    private var isCreatingNewProduct: Boolean = false

    private val debounceScope = CoroutineScope(Dispatchers.Main)
    private val stockUpdateFlow = MutableSharedFlow<Map<String, Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product = it.getSerializable("selectedProduct", Product::class.java)
            isCreatingNewProduct = it.getBoolean("isCreatingNewProduct", false)

            if (isCreatingNewProduct) handleClickBtnEditProduct()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTextViews()

        binding.btnEditProduct.setOnClickListener { handleClickBtnEditProduct() }
        binding.btnAddStock.setOnClickListener { handleClickBtnAddStock() }
        binding.btnRemoveStock.setOnClickListener { handleClickBtnRemoveStock() }

        // Going to previous activity when back is pressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStack()
            } else {
                requireActivity().finish()
            }
        }

        // Debounce configuration
        debounceScope.launch {
            stockUpdateFlow
                .debounce(500)
                .onEach { updateData ->
                    val stockValue = updateData["stock"] ?: 0
                    updateProduct(product, mapOf("stock" to stockValue))
                }
                .launchIn(this)
        }
    }

    private fun handleClickBtnAddStock() {
        val currentStock = binding.tvStockValue.text.toString().toIntOrNull() ?: 0
        val newStock = currentStock + 1

        binding.tvStockValue.text = newStock.toString()

        debounceScope.launch {
            stockUpdateFlow.emit(mapOf("stock" to newStock))
        }
    }

    private fun handleClickBtnRemoveStock() {
        val currentStock = binding.tvStockValue.text.toString().toIntOrNull() ?: 0
        val newStock = currentStock - if (currentStock == 0) 0 else 1

        binding.tvStockValue.text = newStock.toString()

        debounceScope.launch {
            stockUpdateFlow.emit(mapOf("stock" to newStock))
        }
    }

    private fun updateProduct(product: Product?, newInformation: Map<String, Any>) {
        product?.key?.let {
            productsRef.child(it).updateChildren(newInformation)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Información actualizada correctamente.",
                        Toast.LENGTH_SHORT
                    ).show()
                    parentFragmentManager.popBackStack()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Ocurrió un problema al actualizar la información. Por favor, verifica tu conexión y vuelve a intentarlo.",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    private fun setupTextViews() {
        product?.let {
            binding.tvName.text = applyTextViewFormat(it.name.toString())
            binding.tvDescriptionValue.text = it.description
            binding.tvCategoryValue.text = applyTextViewFormat(it.category.toString())
            binding.tvPriceValue.text = it.price.toString()
            binding.tvStockValue.text = it.stock.toString()
            if (it.imageUrl != null) Glide.with(requireContext()).load(it.imageUrl).into(binding.ivPicture)
        }
    }

    override fun onResume() {
        super.onResume()

        product?.key?.let { it1 ->
            productsRef.child(it1).get()
                .addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()) {
                        product = dataSnapshot.getValue(Product::class.java)
                        product?.key = dataSnapshot.key

                        setupTextViews()
                    } else {
                        Log.d("Firebase", "No existe el producto con key: $it1")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error al obtener el producto: ${e.message}")
                }
        }
    }

    private fun handleClickBtnEditProduct() {
        val editProductFragment = setupEditProductFragment(product)
        replaceFragment(requireActivity().supportFragmentManager,
            R.id.productFragmentsContainer, editProductFragment)
    }

    private fun setupEditProductFragment(selectedProduct: Product?): Fragment {
        val editProductFragment = EditProductFragment()
        val argsEditProductFragment = Bundle()
        argsEditProductFragment.putSerializable("selectedProduct", selectedProduct)
        argsEditProductFragment.putBoolean("isCreatingNewProduct", isCreatingNewProduct)

        editProductFragment.arguments = argsEditProductFragment

        return editProductFragment
    }

}