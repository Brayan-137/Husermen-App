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

        val inventoryFragment = InventoryFragment().apply {
            setHandleClickItemDetails(handleClickItemDetails)
        }

        replaceFragment(supportFragmentManager, R.id.sectionsFragmentsContainer, inventoryFragment)

        binding.bottomNavigationView.setOnItemSelectedListener {
            val nextFragment: Fragment = when (it.itemId) {
                R.id.inventorySection -> inventoryFragment
                R.id.topSellsSection -> TopSellsFragment()
                R.id.mercadoLibreSection -> MercadoLibreFragment()
                R.id.tutorialsSection -> TutorialsFragment()
                R.id.usersOptionsSection -> UserOptionsFragment()
                else -> InventoryFragment()
            }

            replaceFragment(supportFragmentManager, R.id.sectionsFragmentsContainer, nextFragment)
            true
        }
    }


    val handleClickItemDetails: (Item) -> Unit = { item ->
        val itemDetailsIntent = Intent(this, ProductActivity::class.java)
        itemDetailsIntent.putExtra("selectedProduct", item)
        this.startActivity(itemDetailsIntent)
    }

}