package com.example.husermenapp.fragments

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.husermenapp.databinding.FragmentEditTutorialBinding
import com.example.husermenapp.dataclasses.Tutorial
import com.example.husermenapp.utils.FragmentUtils.applyTextViewFormat
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

        setupSpinnerContentType()

        if(!isCreatingNewTutorial) tutorial?.let {
            binding.etName.setText(applyTextViewFormat(it.name.toString()))
            binding.etDescriptionValue.setText(it.description)
            binding.etTopicValue.setText(applyTextViewFormat(it.topic.toString()))
            binding.etContent.setText(it.content)

            when (it.type) {
                "video" -> {
                    binding.spinnerType.setSelection(0)
                    binding.etVideoUrl.setText(it.videoUrl)
                }
                "guía" -> {
                    binding.spinnerType.setSelection(1)
                    binding.etContent.setText(it.content)
                }
            }
        }

        binding.btnSaveTutorial.setOnClickListener { handleClickBtnSaveTutorial() }
        binding.btnDeleteTutorial.setOnClickListener { handleClickBtnDeleteTutorial() }
    }

    private fun setupSpinnerContentType() {
        val contentTypeOptions = listOf("Video", "Guía")
        val contentTypeSpinner: Spinner = binding.spinnerType

        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, contentTypeOptions)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        contentTypeSpinner.adapter = adapter

        binding.spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()

                when (selectedItem) {
                    "Video" -> {
                        binding.walkthrough.visibility = View.GONE
                        binding.video.visibility = View.VISIBLE
                    }
                    "Guía" -> {
                        binding.walkthrough.visibility = View.VISIBLE
                        binding.video.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Not needed
            }
        }
    }

    private fun handleClickBtnSaveTutorial() {
        if (tutorial == null) return

        val currentValues = mapOf(
            "name" to checkField(binding.etName.text.toString())?.lowercase(),
            "description" to checkField(binding.etDescriptionValue.text.toString()),
            "topic" to checkField(binding.etTopicValue.text.toString())?.lowercase(),
            "type" to checkField(binding.spinnerType.selectedItem.toString())?.lowercase(),
            "content" to checkField(binding.etContent.text.toString()),
            "videoUrl" to checkField(binding.etVideoUrl.text.toString())
        )

        for ((key, value) in currentValues) {
            if (value == null) {
                when (key) {
                    "videoUrl" -> if (tutorial?.type == "video") {
                        Toast.makeText(
                            requireContext(),
                            "Todos los campos deben estar completos.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    "content" -> if (tutorial?.type == "guía") {
                        Toast.makeText(
                            requireContext(),
                            "Todos los campos deben estar completos.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    else -> {
                        Toast.makeText(
                            requireContext(),
                            "Todos los campos deben estar completos.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                }
            }
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
                    "content" to it.content,
                    "videoUrl" to it.videoUrl
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