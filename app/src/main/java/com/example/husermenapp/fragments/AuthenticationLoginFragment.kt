package com.example.husermenapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.husermenapp.MainActivity
import com.example.husermenapp.R
import com.example.husermenapp.databinding.FragmentAuthenticationLoginBinding
import com.example.husermenapp.utils.FragmentUtils.replaceFragment
import com.google.firebase.auth.FirebaseAuth

class AuthenticationLoginFragment : Fragment() {
    private var _binding: FragmentAuthenticationLoginBinding? = null
    private val binding get() = _binding!!

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthenticationLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnLogin.setOnClickListener { handleClickBtnLogin() }
        binding.btnSignup.setOnClickListener { handleClickBtnSignup() }
    }

    private fun handleClickBtnSignup() {
        replaceFragment(
            requireActivity().supportFragmentManager,
            R.id.authFragments,
            AuthenticationSignupFragment()
        )
    }

    private fun handleClickBtnLogin() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Uno o dos campos vacíos. Complete ambos campos e inténtelo de nuevo, por favor.", Toast.LENGTH_LONG).show()
        } else {
            login(email, password)
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Se ha iniciado sesión exitosamente.", Toast.LENGTH_SHORT).show()
                val inventoryIntent = Intent(requireContext(), MainActivity::class.java)
                requireActivity().finish()
                inventoryIntent.putExtra("user", auth.currentUser)
                startActivity(inventoryIntent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Credenciales inválidas. Inténtelo de nuevo, por favor.", Toast.LENGTH_LONG).show()
                binding.etEmail.setText("")
                binding.etPassword.setText("")
            }
    }

}