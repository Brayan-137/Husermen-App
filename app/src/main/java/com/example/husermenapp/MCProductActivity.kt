package com.example.husermenapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.husermenapp.databinding.ActivityMcproductBinding
import com.example.husermenapp.dataclasses.MCProduct
import com.example.husermenapp.fragments.DetailsMCProductFragment
import com.example.husermenapp.utils.FragmentUtils.replaceFragment

class MCProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMcproductBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMcproductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        val mcProductDetailsFragment = setupMCProductDetailsFragment()
        
        replaceFragment(supportFragmentManager, R.id.mcProductFragmentsContainer, mcProductDetailsFragment, false)
    }

    private fun setupMCProductDetailsFragment(): DetailsMCProductFragment {
        val detailsMCProductFragment = DetailsMCProductFragment()
        val argsMCProductDetailsFragment = Bundle()

        val selectedMCProduct = intent.getSerializableExtra("selectedMCProduct", MCProduct::class.java)

        argsMCProductDetailsFragment.putSerializable("selectedMCProduct", selectedMCProduct)
        detailsMCProductFragment.arguments = argsMCProductDetailsFragment

        return detailsMCProductFragment
    }
}