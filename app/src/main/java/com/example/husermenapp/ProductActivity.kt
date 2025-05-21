package com.example.husermenapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.husermenapp.fragments.FragmentUtils.replaceFragment
import com.example.husermenapp.databinding.ActivityProductBinding
import com.example.husermenapp.dataclasses.Product
import com.example.husermenapp.fragments.ProductDetailsFragment

class ProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductBinding

    private lateinit var productDetailsFragment: ProductDetailsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productDetailsFragment = setupProductDetailsFragment()

        replaceFragment(supportFragmentManager, R.id.productFragmentsContainer, productDetailsFragment, false) // Por qué el ID es diferente según use R.id o binding

    }

    private fun setupProductDetailsFragment(): ProductDetailsFragment {
        val productDetailsFragment = ProductDetailsFragment()
        val argsProductDetailsFragment = Bundle()

        val selectedProduct = intent.getSerializableExtra("selectedProduct", Product::class.java)
        val isCreatingNewProduct = intent.getBooleanExtra("isCreatingNewProduct", false)

        argsProductDetailsFragment.putSerializable("selectedProduct", selectedProduct)
        argsProductDetailsFragment.putBoolean("isCreatingNewProduct", isCreatingNewProduct)
        productDetailsFragment.arguments = argsProductDetailsFragment

        return productDetailsFragment
    }
}