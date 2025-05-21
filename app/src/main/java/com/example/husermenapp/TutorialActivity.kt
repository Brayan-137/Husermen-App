package com.example.husermenapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.husermenapp.fragments.FragmentUtils.replaceFragment
import com.example.husermenapp.databinding.ActivityTutorialBinding
import com.example.husermenapp.dataclasses.Tutorial
import com.example.husermenapp.fragments.TutorialDetailsFragment

class TutorialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTutorialBinding

    private lateinit var tutorialDetailsFragment: TutorialDetailsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tutorialDetailsFragment = setupTutorialDetailsFragment()

        replaceFragment(supportFragmentManager, R.id.tutorialFragmentsContainer, tutorialDetailsFragment, false) // Por qué el ID es diferente según use R.id o binding

    }

    private fun setupTutorialDetailsFragment(): TutorialDetailsFragment {
        val tutorialDetailsFragment = TutorialDetailsFragment()
        val argsTutorialDetailsFragment = Bundle()

        val selectedTutorial = intent.getSerializableExtra("selectedTutorial", Tutorial::class.java)
        val isCreatingNewTutorial = intent.getBooleanExtra("isCreatingNewTutorial", false)

        argsTutorialDetailsFragment.putSerializable("selectedTutorial", selectedTutorial)
        argsTutorialDetailsFragment.putBoolean("isCreatingNewTutorial", isCreatingNewTutorial)
        tutorialDetailsFragment.arguments = argsTutorialDetailsFragment

        return tutorialDetailsFragment
    }
}