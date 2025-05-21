package com.example.husermenapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.husermenapp.databinding.FragmentEditTutorialBinding
import com.example.husermenapp.dataclasses.Tutorial
import com.example.husermenapp.fragments.FragmentUtils.applyTextViewFormat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditTutorialFragment : BaseEditItemFragment<FragmentEditTutorialBinding>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditTutorialBinding = FragmentEditTutorialBinding.inflate(inflater, container, false)

    private val modelRef: String = "tutorials"
    private val tutorialsRef: DatabaseReference = FirebaseDatabase.getInstance().getReference(modelRef)
    private var tutorial: Tutorial? = null
    private var isCreatingNewTutorial: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tutorial = it.getSerializable("selectedTutorial", Tutorial::class.java)
            isCreatingNewTutorial = it.getBoolean("isCreatingNewTutorial", false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!isCreatingNewTutorial) tutorial?.let {
            binding.etName.setText(applyTextViewFormat(it.name.toString()))
            binding.etDescriptionValue.setText(it.description)
            binding.etTopicValue.setText(applyTextViewFormat(it.topic.toString()))
            binding.etTypeValue.setText(applyTextViewFormat(it.type.toString()))
            binding.etContent.setText(it.content)
        }

        binding.btnSaveTutorial.setOnClickListener { handleClickBtnSaveTutorial() }
        binding.btnDeleteTutorial.setOnClickListener { handleClickBtnDeleteTutorial() }
    }

    private fun handleClickBtnSaveTutorial() {
        if (tutorial == null) return

        val currentValues = mapOf(
            "name" to checkField(binding.etName.text)?.lowercase(),
            "description" to checkField(binding.etDescriptionValue.text),
            "topic" to checkField(binding.etTopicValue.text)?.lowercase(),
            "type" to checkField(binding.etTypeValue.text)?.lowercase(),
            "content" to checkField(binding.etContent.text)
        )

        for (value in currentValues.values) {
            if (value == null) {
                Toast.makeText(
                    requireContext(),
                    "Todos los campos deben estar completos.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            break
        }

        if (isCreatingNewTutorial) {
            // Creating a new tutorial
            tutorial?.key?.let {
                tutorialsRef.child(it).updateChildren(currentValues)
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Tutorial creado correctamente.",
                            Toast.LENGTH_SHORT
                        ).show()
                        parentFragmentManager.popBackStack()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Ocurrió un problema al crear el tutorial. Por favor, verifica tu conexión y vuelve a intentarlo.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        } else {
            // Updating the tutorial's information
            val oldValues = tutorial?.let {
                mapOf(
                    "name" to it.name,
                    "description" to it.description,
                    "topic" to it.topic,
                    "type" to it.type,
                    "content" to it.content
                )
            }

            val newInformation = currentValues.filter { (key, currentValue) ->
                oldValues?.get(key) != currentValue
            }

            if (newInformation.isNotEmpty()) {
                tutorial?.key?.let {
                    tutorialsRef.child(it).updateChildren(newInformation)
                        .addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                "Información actualizada correctamente.",
                                Toast.LENGTH_SHORT
                            ).show()
                            parentFragmentManager.popBackStack()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                requireContext(),
                                "Ocurrió un problema al actualizar la información. Por favor, verifica tu conexión y vuelve a intentarlo.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }
        }
    }

    private fun handleClickBtnDeleteTutorial() {
        TODO("Not yet implemented")
    }
}