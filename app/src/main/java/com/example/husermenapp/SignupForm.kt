package com.example.husermenapp

import android.R
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.husermenapp.databinding.ActivitySignupFormBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupForm : AppCompatActivity() {
    private lateinit var binding: ActivitySignupFormBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userTypeOptions = listOf("Normal", "Administrador")
        val userTypeSpinner: Spinner = binding.spinnerUserType

        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, userTypeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        userTypeSpinner.adapter = adapter

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
            Toast.makeText(this, "Uno o dos campos vacíos. Complete ambos campos e inténtelo de nuevo, por favor.", Toast.LENGTH_LONG).show()
        } else if (password != confirmPassword) {
            Toast.makeText(this, "La contraseña no coinciden. Verifiquela e inténtelo de nuevo, por favor.", Toast.LENGTH_LONG).show()
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
                Toast.makeText(this, "Usuario $names creado exitosamente.", Toast.LENGTH_SHORT).show()

                authResult.user?.uid?.let { addUserToDatabase(it, names, lastNames, phone, email, userType) }
            }
            .addOnFailureListener { e ->
                val errorMessage = when (e) {
                    is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil."
                    is FirebaseAuthInvalidCredentialsException -> "El correo electrónico no es válido."
                    is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con este correo."
                    else -> "Error desconocido: ${e?.localizedMessage}"
                }

                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
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
        database.child("users").child(uid).setValue(user)
    }
}