package com.diplomaproject.neristbuddy

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity



class LoginActivity : AppCompatActivity() {
    lateinit var btnlogin: Button
    lateinit var etId: EditText
    lateinit var etPassword: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnlogin = findViewById(R.id.btnLogin)
        etId = findViewById(R.id.etId)
        etPassword = findViewById(R.id.etPassword)
    }
}