package com.example.husermenapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.husermenapp.FragmentUtils.replaceFragment
import com.example.husermenapp.databinding.ActivityMainBinding
import com.example.husermenapp.databinding.ActivityProductBinding

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

        val selectedProduct = intent.getSerializableExtra("selectedProduct", Item::class.java)
        val isCreatingNewProduct = intent.getBooleanExtra("isCreatingNewProduct", false)

        argsProductDetailsFragment.putSerializable("selectedProduct", selectedProduct)
        argsProductDetailsFragment.putBoolean("isCreatingNewProduct", isCreatingNewProduct)
        productDetailsFragment.arguments = argsProductDetailsFragment

        return productDetailsFragment
    }
}