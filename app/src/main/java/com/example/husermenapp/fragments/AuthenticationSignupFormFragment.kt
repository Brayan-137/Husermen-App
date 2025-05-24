package com.example.husermenapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.husermenapp.R
import com.example.husermenapp.databinding.FragmentAuthenticationSignupFormBinding
import com.example.husermenapp.dataclasses.User
import com.example.husermenapp.utils.FragmentUtils.replaceFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AuthenticationSignupFormFragment : Fragment() {
    private var _binding: FragmentAuthenticationSignupFormBinding? = null
    private val binding get() = _binding!!

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userEmail = it.getString("email")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthenticationSignupFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userTypeOptions = listOf("Normal", "Administrador")
        val userTypeSpinner: Spinner = binding.spinnerUserType

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, userTypeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        userTypeSpinner.adapter = adapter

        binding.etEmail.setText(userEmail)

        binding.btnConfirmSignUp.setOnClickListener { handleClickBtnConfirmSignUp() }
    }


    private fun handleClickBtnConfirmSignUp() {
        val names = binding.etName.text.toString()
        val lastNames = binding.etLastName.text.toString()
        val phone = binding.etPhone.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()
        val userType = binding.spinnerUserType.selectedItem.toString()

        if (names.isEmpty() || lastNames.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Uno o dos campos vacíos. Complete ambos campos e inténtelo de nuevo, por favor.", Toast.LENGTH_LONG).show()
        } else if (password != confirmPassword) {
            Toast.makeText(requireContext(), "La contraseña no coinciden. Verifiquela e inténtelo de nuevo, por favor.", Toast.LENGTH_LONG).show()
        } else {
            signUpUser(names, lastNames, phone, email, password, userType)
        }
    }

    private fun signUpUser(
        names: String,
        lastNames: String,
        phone: String,
        email: String,
        password: String,
        userType: String
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                Toast.makeText(requireContext(), "Usuario $names creado exitosamente.", Toast.LENGTH_SHORT).show()

                authResult.user?.uid?.let { addUserToDatabase(it, names, lastNames, phone, email, userType) }

                replaceFragment(
                    requireActivity().supportFragmentManager,
                    R.id.authFragments,
                    AuthenticationLoginFragment()
                )
            }
            .addOnFailureListener { e ->
                val errorMessage = when (e) {
                    is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil."
                    is FirebaseAuthInvalidCredentialsException -> "El correo electrónico no es válido."
                    is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con este correo."
                    else -> "Error desconocido: ${e.localizedMessage}"
                }

                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                println("Error: $e")
            }
    }

    private fun addUserToDatabase(
        uid: String,
        names: String,
        lastNames: String,
        phone: String,
        email: String,
        userType: String
    ) {
        val user = User(uid, names, lastNames, phone, email, userType)
        usersRef.child(uid).setValue(user)
    }
}