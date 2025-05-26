package com.example.husermenapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.husermenapp.utils.FragmentUtils.replaceFragment
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

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        detailsProductFragment = setupProductDetailsFragment()

        replaceFragment(
            supportFragmentManager,
            R.id.productFragmentsContainer,
            detailsProductFragment,
            false
        ) // Por qué el ID es diferente según use R.id o binding

    }

    private fun setupProductDetailsFragment(): DetailsProductFragment {
        val detailsProductFragment = DetailsProductFragment()
        val argsProductDetailsFragment = Bundle()

        val selectedProduct = intent.getSerializableExtra("selectedProduct") as? Product
        val isCreatingNewProduct = intent.getBooleanExtra("isCreatingNewProduct", false)

        argsProductDetailsFragment.putSerializable("selectedProduct", selectedProduct)
        argsProductDetailsFragment.putBoolean("isCreatingNewProduct", isCreatingNewProduct)
        detailsProductFragment.arguments = argsProductDetailsFragment

        return detailsProductFragment
    }
}