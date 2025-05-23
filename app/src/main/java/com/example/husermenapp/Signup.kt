package com.example.husermenapp

import android.content.Intent
import android.os.Bundle
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.husermenapp.databinding.ActivitySignupBinding
import com.example.husermenapp.databinding.ActivitySignupFormBinding

class Signup : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnSignupForm.setOnClickListener { handleClickBtnSignupForm() }
    }

    private fun handleClickBtnSignupForm() {
        val email = binding.etEmail.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(this, "Correo electrónico vacío. Complete el campo e inténtelo de nuevo, por favor.", Toast.LENGTH_LONG).show()
        } else {
            val signupFormIntent = Intent(this, SignupForm::class.java)
            signupFormIntent.putExtra("email", email)
            finish()
            startActivity(signupFormIntent)
        }
    }
}