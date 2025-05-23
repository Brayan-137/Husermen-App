package com.example.husermenapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.husermenapp.utils.FragmentUtils.replaceFragment
import com.example.husermenapp.databinding.ActivityMainBinding
import com.example.husermenapp.dataclasses.MCProduct
import com.example.husermenapp.dataclasses.Product
import com.example.husermenapp.dataclasses.Tutorial
import com.example.husermenapp.fragments.SectionInventoryFragment
import com.example.husermenapp.fragments.SectionMercadoLibreFragment
import com.example.husermenapp.fragments.SectionTopSellsFragment
import com.example.husermenapp.fragments.SectionTutorialsFragment
import com.example.husermenapp.fragments.SectionUserOptionsFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportFragmentManager.addOnBackStackChangedListener { handleBackStackChanged() }

        val sectionInventoryFragment =
            SectionInventoryFragment().apply { setHandleClickItemDetails(handleClickItemDetails) }
        val sectionTutorialFragment =
            SectionTutorialsFragment().apply { setHandleClickItemDetails(handleClickTutorialDetails) }
        val sectionMercadoLibreFragment = SectionMercadoLibreFragment().apply {
            setHandleClickItemDetails(handleClickMCProductDetails)
        }
        val sectionTopSellsFragment =
            SectionTopSellsFragment().apply { setHandleClickItemDetails(handleClickMCProductDetails) }

        replaceFragment(
            supportFragmentManager,
            R.id.sectionsFragmentsContainer,
            sectionInventoryFragment,
            false
        )

        binding.bottomNavigationView.setOnItemSelectedListener {
            val nextFragment: Fragment? = when (it.itemId) {
                R.id.inventorySection -> sectionInventoryFragment
                R.id.topSellsSection -> sectionTopSellsFragment
                R.id.mercadoLibreSection -> sectionMercadoLibreFragment
                R.id.tutorialsSection -> sectionTutorialFragment
                R.id.usersOptionsSection -> {
                    confirmLogout()
                    null
                }
                else -> SectionInventoryFragment()
            }

            if (nextFragment != null) {
                replaceFragment(
                    supportFragmentManager,
                    R.id.sectionsFragmentsContainer,
                    nextFragment,
                    isAddToBackStack = nextFragment != sectionInventoryFragment
                )
            }

            true
        }
    }

    private fun confirmLogout() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro de que quieres salir?")
            .setPositiveButton("Sí") { _, _ ->
                performLogout()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun performLogout() {
        FirebaseAuth.getInstance().signOut()

        startActivity(Intent(this, Login::class.java))
        this.finish()
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