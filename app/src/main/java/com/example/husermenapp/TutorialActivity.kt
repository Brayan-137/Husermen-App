package com.example.husermenapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.husermenapp.utils.FragmentUtils.replaceFragment
import com.example.husermenapp.databinding.ActivityTutorialBinding
import com.example.husermenapp.dataclasses.Tutorial
import com.example.husermenapp.fragments.DetailsTutorialFragment

class TutorialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTutorialBinding

    private lateinit var detailsTutorialFragment: DetailsTutorialFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        detailsTutorialFragment = setupTutorialDetailsFragment()

        replaceFragment(supportFragmentManager, R.id.tutorialFragmentsContainer, detailsTutorialFragment, false) // Por qué el ID es diferente según use R.id o binding

    }

    private fun setupTutorialDetailsFragment(): DetailsTutorialFragment {
        val detailsTutorialFragment = DetailsTutorialFragment()
        val argsTutorialDetailsFragment = Bundle()

        val selectedTutorial = intent.getSerializableExtra("selectedTutorial", Tutorial::class.java)
        val isCreatingNewTutorial = intent.getBooleanExtra("isCreatingNewTutorial", false)

        argsTutorialDetailsFragment.putSerializable("selectedTutorial", selectedTutorial)
        argsTutorialDetailsFragment.putBoolean("isCreatingNewTutorial", isCreatingNewTutorial)
        detailsTutorialFragment.arguments = argsTutorialDetailsFragment

        return detailsTutorialFragment
    }
}