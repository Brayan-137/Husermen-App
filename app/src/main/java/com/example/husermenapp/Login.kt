package com.example.husermenapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.husermenapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener { handleClickBtnLogin() }
        binding.btnSignup.setOnClickListener { handleClickBtnSignup() }
    }

    private fun handleClickBtnSignup() {
        startActivity(Intent(this, Signup::class.java))
    }

    private fun handleClickBtnLogin() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Uno o dos campos vacíos. Complete ambos campos e inténtelo de nuevo, por favor.", Toast.LENGTH_LONG).show()
        } else {
            login(email, password)
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Toast.makeText(this, "Se ha iniciado sesión exitosamente.", Toast.LENGTH_SHORT).show()
                finish()
                startActivity(Intent(this, Inventory::class.java))
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Credenciales inválidas. Inténtelo de nuevo, por favor.", Toast.LENGTH_LONG).show()
                binding.etEmail.setText("")
                binding.etPassword.setText("")
            }
    }
}