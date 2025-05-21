package com.example.husermenapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.husermenapp.fragments.FragmentUtils.replaceFragment
import com.example.husermenapp.databinding.ActivityProductBinding
import com.example.husermenapp.dataclasses.Product
import com.example.husermenapp.fragments.DetailsProductFragment

class ProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductBinding

    private lateinit var detailsProductFragment: DetailsProductFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detailsProductFragment = setupProductDetailsFragment()

        replaceFragment(supportFragmentManager, R.id.productFragmentsContainer, detailsProductFragment, false) // Por qué el ID es diferente según use R.id o binding

    }

    private fun setupProductDetailsFragment(): DetailsProductFragment {
        val detailsProductFragment = DetailsProductFragment()
        val argsProductDetailsFragment = Bundle()

        val selectedProduct = intent.getSerializableExtra("selectedProduct", Product::class.java)
        val isCreatingNewProduct = intent.getBooleanExtra("isCreatingNewProduct", false)

        argsProductDetailsFragment.putSerializable("selectedProduct", selectedProduct)
        argsProductDetailsFragment.putBoolean("isCreatingNewProduct", isCreatingNewProduct)
        detailsProductFragment.arguments = argsProductDetailsFragment

        return detailsProductFragment
    }
}