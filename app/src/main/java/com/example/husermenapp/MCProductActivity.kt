package com.example.husermenapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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