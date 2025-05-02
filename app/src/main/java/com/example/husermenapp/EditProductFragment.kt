package com.example.husermenapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.husermenapp.FragmentUtils.applyTextViewFormat
import com.example.husermenapp.databinding.FragmentEditProductBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class EditProductFragment : Fragment() {
    private var _binding: FragmentEditProductBinding?= null
    private val binding get() = _binding!!

    private val productsRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("items")
    private var product: Item? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product = it.getSerializable("selectedProduct", Item::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        product?.let {
            binding.etName.setText(applyTextViewFormat(it.name.toString()))
            binding.etDescriptionValue.setText(it.description)
            binding.etCategoryValue.setText(applyTextViewFormat(it.category.toString()))
            binding.etPriceValue.setText(it.price.toString())
            binding.etStockValue.setText(it.stock.toString())
        }

        binding.btnSaveProduct.setOnClickListener { handleClickBtnSaveProduct() }
        binding.btnDeleteProduct.setOnClickListener { handleClickBtnDeleteProduct() }

        // TODO: Deshabilitar boton en caso de que no haya cambios.
    }

    private fun handleClickBtnDeleteProduct() {
        TODO("Not yet implemented")
    }

    private fun handleClickBtnSaveProduct() {
        if (product == null) return

        if (product?.key == null) {
            // Registering a new product
        } else {
            // Updating the product's information
            val oldValues = product?.let {
                mapOf(
                    "name" to it.name,
                    "description" to it.description,
                    "category" to it.category,
                    "price" to it.price,
                    "stock" to it.stock
                )
            }

            val currentValues = mapOf(
                "name" to binding.etName.text.toString(),
                "description" to binding.etDescriptionValue.text.toString(),
                "category" to binding.etCategoryValue.text.toString(),
                "price" to binding.etPriceValue.text.toString().toIntOrNull(),
                "stock" to binding.etStockValue.text.toString().toIntOrNull()
            )

            val newInformation = currentValues.filter { (key, currentValue) ->
                oldValues?.get(key) != currentValue
            }

            if (newInformation.isNotEmpty()) {
                product?.key?.let {
                    productsRef.child(it).updateChildren(newInformation)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Información actualizada correctamente.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Ocurrió un problema al actualizar la información. Por favor, verifica tu conexión y vuelve a intentarlo.", Toast.LENGTH_LONG).show()
                        }
                }
            }
        }
    }
}