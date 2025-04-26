package com.example.husermenapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.husermenapp.databinding.ActivityMainBinding
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(InventoryFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {
            val nextFragment: Fragment = when (it.itemId) {
                R.id.inventorySection -> InventoryFragment()
                R.id.topSellsSection -> TopSellsFragment()
                R.id.mercadoLibreSection -> MercadoLibreFragment()
                R.id.tutorialsSection -> TutorialsFragment()
                R.id.usersOptionsSection -> UserOptionsFragment()
                else -> InventoryFragment()
            }

            replaceFragment(nextFragment)
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.sectionsFragmentContainer, fragment)
        fragmentTransition.commit()
    }
}