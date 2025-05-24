package com.example.husermenapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.husermenapp.R
import com.example.husermenapp.databinding.FragmentAuthenticationSignupBinding
import com.example.husermenapp.utils.FragmentUtils.replaceFragment

class AuthenticationSignupFragment : Fragment() {
    private var _binding: FragmentAuthenticationSignupBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthenticationSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignupForm.setOnClickListener { handleClickBtnSignupForm() }
    }

    private fun handleClickBtnSignupForm() {
        val email = binding.etEmail.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "Correo electrónico vacío. Complete el campo e inténtelo de nuevo, por favor.", Toast.LENGTH_LONG).show()
        } else {
            replaceFragment(
                requireActivity().supportFragmentManager,
                R.id.authFragments,
                AuthenticationSignupFormFragment()
            )
        }
    }
}