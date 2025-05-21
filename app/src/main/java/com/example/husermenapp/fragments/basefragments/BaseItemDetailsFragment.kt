package com.example.husermenapp.fragments.basefragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.husermenapp.fragments.FragmentUtils.applyTextViewFormat
import java.io.Serializable

abstract class BaseItemDetailsFragment<VB: ViewBinding, T: Any> : Fragment() {
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB
    protected abstract fun setupTextViews()
    protected abstract fun EditItemFragment(): Fragment

    protected abstract var modelRef: String
    protected var isCreatingNewItem: Boolean = false

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
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTextViews()

        // Going to previous activity when back is pressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStack()
            } else {
                requireActivity().finish()
            }
        }

        // TODO: deshabilitar el botón de información ventas a menos que este vinculado a un producto de mercado libre
    }

    open protected fun <T: Serializable>  setupEditItemFragment(selectedItem: T?): Fragment {
        val editItemFragment = EditItemFragment()
        val argsEditItemFragment = Bundle()
        val capitalizedModelRef = applyTextViewFormat(modelRef)
        argsEditItemFragment.putSerializable("selected${capitalizedModelRef.dropLast(1)}", selectedItem)
        argsEditItemFragment.putBoolean("isCreatingNew${capitalizedModelRef.dropLast(1)}", isCreatingNewItem)

        editItemFragment.arguments = argsEditItemFragment

        return editItemFragment
    }
}