package com.example.husermenapp.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.husermenapp.databinding.FragmentEditProductBinding
import com.example.husermenapp.dataclasses.Product
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.husermenapp.utils.FragmentUtils.applyTextViewFormat
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class EditProductFragment : Fragment() {
    private var _binding: FragmentEditProductBinding?= null
    private val binding get() = _binding!!

    private val productsRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("items")
    private val storageRef: StorageReference = FirebaseStorage.getInstance().getReference()
    private var product: Product? = null
    private var isCreatingNewProduct: Boolean = false

    private lateinit var originalValues: Map<String, Any?>

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product = it.getSerializable("selectedProduct") as? Product
            isCreatingNewProduct = it.getBoolean("isCreatingNewProduct", false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProductBinding.inflate(inflater, container, false)

        // When onBack is pressed product is not created and user is redirect to mainActivity
        if (isCreatingNewProduct) {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPickImageLauncher()

        originalValues = product?.let {
            mapOf<String, Any?>(
                "name" to it.name,
                "description" to it.description,
                "category" to it.category,
                "price" to it.price,
                "stock" to it.stock,
                "imageUrl" to it.imageUrl
            )
        } ?: emptyMap()

        if (!isCreatingNewProduct) product?.let {
            binding.etName.setText(applyTextViewFormat(it.name.toString()))
            binding.etDescriptionValue.setText(it.description)
            binding.etCategoryValue.setText(applyTextViewFormat(it.category.toString()))
            binding.etPriceValue.setText(it.price.toString())
            binding.etStockValue.setText(it.stock.toString())
            if (it.imageUrl != null) Glide.with(requireContext())
                .load(it.imageUrl)
                .transform(
                    MultiTransformation(
                        CenterCrop(),
                        RoundedCorners(16)
                    )
                )
                .into(binding.ivPicture)
        }

        binding.btnSaveProduct.setOnClickListener { handleClickBtnSaveProduct() }
        binding.btnDeleteProduct.setOnClickListener { handleClickBtnDeleteProduct() }
        binding.ivPicture.setOnClickListener { handleClickIvPicture() }

        // TODO: Deshabilitar boton en caso de que no haya cambios.
    }

    private fun setupPickImageLauncher() {
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                selectedImageUri = data?.data
                selectedImageUri?.let { uri ->
                    Glide.with(requireContext()).load(uri).into(binding.ivPicture)

                    val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

                    imageRef.putFile(uri).addOnSuccessListener {
                        imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                            Log.d("Firebase", "URL: $downloadUrl")
                            product?.imageUrl = downloadUrl.toString()
                        }
                    }
                }
            }
        }
    }

    private fun handleClickIvPicture() {
        pickImageLauncher.launch(Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        })
    }

    private fun handleClickBtnDeleteProduct() {
        product?.key?.let {
            productsRef.child(it).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Producto eliminado correctamente.",
                        Toast.LENGTH_SHORT
                    ).show()
                    requireActivity().finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Error al eliminar el producto: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    private fun handleClickBtnSaveProduct() {
        if (product == null) return

        val currentValues = mapOf(
            "name" to checkField(binding.etName.text)?.lowercase(),
            "description" to checkField(binding.etDescriptionValue.text),
            "category" to checkField(binding.etCategoryValue.text)?.lowercase(),
            "price" to checkField(binding.etPriceValue.text)?.toIntOrNull(),
            "stock" to checkField(binding.etStockValue.text)?.toIntOrNull(),
            "imageUrl" to product?.imageUrl
        )

        for ((key, value) in currentValues) {
            if (value == null) {
                when (key) {
                    "price" -> Toast.makeText(
                        requireContext(),
                        "El precio debe ser un número. Por favor, verifique el valor e intentelo nuevamente.",
                        Toast.LENGTH_LONG
                    ).show()
                    "stock" -> Toast.makeText(
                        requireContext(),
                        "El stock debe ser un número. Por favor, verifique el valor e intentelo nuevamente.",
                        Toast.LENGTH_SHORT
                    ).show()
                    "imageUrl" -> {
                        // Upload and image is not necesary
                    }
                    else -> Toast.makeText(
                        requireContext(),
                        "Todos los campos deben estar completos.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }

        if (isCreatingNewProduct) {
            // Registering a new product
            product?.key?.let {
                productsRef.child(it).updateChildren(currentValues)
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Producto registrado correctamente.",
                            Toast.LENGTH_SHORT
                        ).show()
                        parentFragmentManager.popBackStack()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Ocurrió un problema al registrar el producto. Por favor, verifica tu conexión y vuelve a intentarlo.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        } else {
            // Updating the product's information
            val newInformation = currentValues.filter { (key, currentValue) ->
                originalValues?.get(key) != currentValue
            }

            Log.d("EditProduct", newInformation.toString())

            if (newInformation.isNotEmpty()) {
                product?.key?.let {
                    productsRef.child(it).updateChildren(newInformation)
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

    private fun checkField(text: Editable?): String? {
        return if (text.isNullOrBlank()) null else text.toString()
    }
}