package com.example.husermenapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.husermenapp.FragmentUtils.replaceFragment
import com.example.husermenapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.addOnBackStackChangedListener { handleBackStackChanged() }

        val inventoryFragment = InventoryFragment().apply { setHandleClickItemDetails(handleClickItemDetails) }
        val tutorialFragment = TutorialsFragment().apply { setHandleClickItemDetails(handleClickTutorialDetails) }
        val mercadoLibreFragment = MercadoLibreFragment().apply { setHandleClickItemDetails(handleClickMCProductDetails) }

        replaceFragment(supportFragmentManager, R.id.sectionsFragmentsContainer, inventoryFragment, false)

        binding.bottomNavigationView.setOnItemSelectedListener {
            val nextFragment: Fragment = when (it.itemId) {
                R.id.inventorySection -> inventoryFragment
                R.id.topSellsSection -> TopSellsFragment()
                R.id.mercadoLibreSection -> mercadoLibreFragment
                R.id.tutorialsSection -> tutorialFragment
                R.id.usersOptionsSection -> UserOptionsFragment()
                else -> InventoryFragment()
            }

            replaceFragment(
                supportFragmentManager,
                R.id.sectionsFragmentsContainer,
                nextFragment,
                isAddToBackStack = nextFragment != inventoryFragment
            )
            true
        }
    }

    private fun handleBackStackChanged() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.sectionsFragmentsContainer)

        when (currentFragment) {
            is InventoryFragment -> binding.bottomNavigationView.menu.findItem(R.id.inventorySection).isChecked = true
            is TopSellsFragment -> binding.bottomNavigationView.menu.findItem(R.id.topSellsSection).isChecked = true
            is MercadoLibreFragment -> binding.bottomNavigationView.menu.findItem(R.id.mercadoLibreSection).isChecked = true
            is TutorialsFragment -> binding.bottomNavigationView.menu.findItem(R.id.tutorialsSection).isChecked = true
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
        val itemDetailsIntent = Intent(this, MCProduct::class.java)
        itemDetailsIntent.putExtra("seletedMCProduct", mcProduct)
        this.startActivity(itemDetailsIntent)
    }

}