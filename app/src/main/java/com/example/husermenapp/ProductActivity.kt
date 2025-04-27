package com.example.husermenapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.husermenapp.FragmentUtils.replaceFragment
import com.example.husermenapp.databinding.ActivityMainBinding

class ProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedProduct = intent.getSerializableExtra("selectedProduct", Item::class.java)

        val productDetailsFragment = ProductDetailsFragment()
        val argsProductDetailsFragment = Bundle()
        argsProductDetailsFragment.putSerializable("selectedItem", selectedProduct)
        productDetailsFragment.arguments = argsProductDetailsFragment

        replaceFragment(supportFragmentManager, binding.sectionsFragmentsContainer.id, productDetailsFragment) // Por qué el ID es diferente según use R.id o binding
    }
}