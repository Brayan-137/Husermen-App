package com.example.husermenapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.husermenapp.FragmentUtils.applyTextViewFormat
import com.example.husermenapp.FragmentUtils.replaceFragment
import com.example.husermenapp.databinding.FragmentTutorialDetailsBinding
import com.example.husermenapp.databinding.FragmentTutorialsBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TutorialDetailsFragment : BaseItemDetailsFragment<FragmentTutorialDetailsBinding, Tutorial>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTutorialDetailsBinding {
        return FragmentTutorialDetailsBinding.inflate(inflater, container, false)
    }

    override fun setupTextViews() {
        tutorial?.let {
            binding.tvName.text = applyTextViewFormat(it.name.toString())
            binding.tvDescriptionValue.text = it.description
            binding.tvTopicValue.text = applyTextViewFormat(it.topic.toString())
            binding.tvTypeValue.text = applyTextViewFormat(it.type.toString())
            binding.tvContent.text = it.content
        }
    }

    override fun EditItemFragment(): Fragment = EditTutorialFragment()

    override var modelRef: String = "tutorials"

    private var tutorial: Tutorial? = null
    private val tutorialsRef: DatabaseReference = FirebaseDatabase.getInstance().getReference(modelRef)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tutorial = it.getSerializable("selectedTutorial", Tutorial::class.java)
            isCreatingNewItem = it.getBoolean("isCreatingNewTutorial", false)

            if (isCreatingNewItem) handleClickBtnEditTutorial()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTextViews()

        binding.btnEditTutorial.setOnClickListener { handleClickBtnEditTutorial() }
    }

    override fun onResume() {
        super.onResume()

        tutorial?.key?.let { it1 ->
            tutorialsRef.child(it1).get()
                .addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()) {
                        tutorial = dataSnapshot.getValue(Tutorial::class.java)
                        tutorial?.key = dataSnapshot.key

                        setupTextViews()
                    } else {
                        Log.d("Firebase", "No existe el producto con key: $it1")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error al obtener el producto: ${e.message}")
                }
        }
    }

    private fun handleClickBtnEditTutorial() {
        val editTutorialFragment = setupEditItemFragment(tutorial)
        replaceFragment(requireActivity().supportFragmentManager, R.id.tutorialFragmentsContainer, editTutorialFragment)
    }
}