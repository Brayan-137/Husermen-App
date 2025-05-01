package com.example.husermenapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.husermenapp.FragmentUtils.replaceFragment
import com.example.husermenapp.databinding.FragmentProductDetailsBinding

class ProductDetailsFragment : Fragment() {
    private var _binding: FragmentProductDetailsBinding? = null
    val binding get() = _binding!!

    private var product: Item? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product = it.getSerializable("selectedProduct", Item::class.java)
            Log.d("ProductDetailsFragment", "Product ${product?.name} exists")
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

        binding.tvName.text = product?.name
        binding.tvDescriptionValue.text = product?.description
        binding.tvPriceValue.text = product?.price.toString()
        binding.tvStockValue.text = product?.stock.toString()

        binding.btnEditProduct.setOnClickListener { handleClickBtnEditProduct() }
    }

    private fun handleClickBtnEditProduct() {
        Log.d("Product Details Fragment", "Click ejecutado")
        val editProductFragment: EditProductFragment = setupEditProductFragment(product)
        replaceFragment(requireActivity().supportFragmentManager, R.id.productFragmentsContainer, editProductFragment)
    }


    private fun setupEditProductFragment(selectedProduct: Item?): EditProductFragment {
        val editProductFragment = EditProductFragment()
        val argsEditProductFragment = Bundle()
        argsEditProductFragment.putSerializable("selectedProduct", selectedProduct)

        editProductFragment.arguments = argsEditProductFragment

        return editProductFragment
    }

}