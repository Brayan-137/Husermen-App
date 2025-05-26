package com.example.husermenapp.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

abstract class BaseEditItemFragment<VB: ViewBinding> : Fragment() {
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    protected val storageRef: StorageReference = FirebaseStorage.getInstance().getReference()

    protected var isCreatingANewItem: Boolean = false

    protected lateinit var originalValues: Map<String, Any?>

    protected lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    protected var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateBinding(inflater, container)

        // When onBack is pressed product is not created and user is redirect to mainActivity
        if (isCreatingANewItem) {
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

    protected fun checkField(text: String): String? {
        return if (text == "") null else text
    }
}