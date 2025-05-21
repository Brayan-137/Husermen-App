package com.example.husermenapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.husermenapp.R
import com.example.husermenapp.databinding.FragmentProductDetailsBinding
import com.example.husermenapp.dataclasses.Product
import com.example.husermenapp.fragments.FragmentUtils.applyTextViewFormat
import com.example.husermenapp.fragments.FragmentUtils.replaceFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DetailsProductFragment : Fragment() {
    private var _binding: FragmentProductDetailsBinding? = null
    val binding get() = _binding!!

    private val productsRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("items")
    private var product: Product? = null
    private var isCreatingNewProduct: Boolean = false

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

        // Going to previous activity when back is pressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStack()
            } else {
                requireActivity().finish()
            }
        }

        // TODO: deshabilitar el botón de información ventas a menos que este vinculado a un producto de mercado libre
    }

    private fun setupTextViews() {
        product?.let {
            binding.tvName.text = applyTextViewFormat(it.name.toString())
            binding.tvDescriptionValue.text = it.description
            binding.tvCategoryValue.text = applyTextViewFormat(it.category.toString())
            binding.tvPriceValue.text = it.price.toString()
            binding.tvStockValue.text = it.stock.toString()
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