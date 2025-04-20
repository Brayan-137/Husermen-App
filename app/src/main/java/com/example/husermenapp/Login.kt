package com.example.husermenapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin: Button =
        btnLogin.setOnClickListener { handleClickBtnLogin() }
    }

    private fun handleClickBtnLogin() {

    }

    private fun login(email: String, password: String) {

    }
}