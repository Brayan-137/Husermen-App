package com.example.husermenapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.husermenapp.fragments.FragmentUtils.replaceFragment
import com.example.husermenapp.databinding.ActivityMainBinding
import com.example.husermenapp.dataclasses.MCProduct
import com.example.husermenapp.dataclasses.Product
import com.example.husermenapp.dataclasses.Tutorial
import com.example.husermenapp.fragments.SectionInventoryFragment
import com.example.husermenapp.fragments.SectionMercadoLibreFragment
import com.example.husermenapp.fragments.SectionTopSellsFragment
import com.example.husermenapp.fragments.SectionTutorialsFragment
import com.example.husermenapp.fragments.SectionUserOptionsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.addOnBackStackChangedListener { handleBackStackChanged() }

        val sectionInventoryFragment = SectionInventoryFragment().apply { setHandleClickItemDetails(handleClickItemDetails) }
        val tutorialFragment = SectionTutorialsFragment().apply { setHandleClickItemDetails(handleClickTutorialDetails) }
        val sectionMercadoLibreFragment = SectionMercadoLibreFragment().apply { setHandleClickItemDetails(handleClickMCProductDetails) }

        replaceFragment(supportFragmentManager, R.id.sectionsFragmentsContainer, sectionInventoryFragment, false)

        binding.bottomNavigationView.setOnItemSelectedListener {
            val nextFragment: Fragment = when (it.itemId) {
                R.id.inventorySection -> sectionInventoryFragment
                R.id.topSellsSection -> SectionTopSellsFragment()
                R.id.mercadoLibreSection -> sectionMercadoLibreFragment
                R.id.tutorialsSection -> tutorialFragment
                R.id.usersOptionsSection -> SectionUserOptionsFragment()
                else -> SectionInventoryFragment()
            }

            replaceFragment(
                supportFragmentManager,
                R.id.sectionsFragmentsContainer,
                nextFragment,
                isAddToBackStack = nextFragment != sectionInventoryFragment
            )
            true
        }
    }

    private fun handleBackStackChanged() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.sectionsFragmentsContainer)

        when (currentFragment) {
            is SectionInventoryFragment -> binding.bottomNavigationView.menu.findItem(R.id.inventorySection).isChecked = true
            is SectionTopSellsFragment -> binding.bottomNavigationView.menu.findItem(R.id.topSellsSection).isChecked = true
            is SectionMercadoLibreFragment -> binding.bottomNavigationView.menu.findItem(R.id.mercadoLibreSection).isChecked = true
            is SectionTutorialsFragment -> binding.bottomNavigationView.menu.findItem(R.id.tutorialsSection).isChecked = true
        }
    }

    private val handleClickItemDetails: (Product) -> Unit = { item ->
        val itemDetailsIntent = Intent(this, ProductActivity::class.java)
        itemDetailsIntent.putExtra("selectedProduct", item)
        this.startActivity(itemDetailsIntent)
    }

    private val handleClickTutorialDetails: (Tutorial) -> Unit = { tutorial ->
        val itemDetailsIntent = Intent(this, TutorialActivity::class.java)
        itemDetailsIntent.putExtra("selectedTutorial", tutorial)
        this.startActivity(itemDetailsIntent)
    }

    private val handleClickMCProductDetails: (MCProduct) -> Unit = { mcProduct ->
        val itemDetailsIntent = Intent(this, MCProductActivity::class.java)
        itemDetailsIntent.putExtra("selectedMCProduct", mcProduct)
        this.startActivity(itemDetailsIntent)
    }

}