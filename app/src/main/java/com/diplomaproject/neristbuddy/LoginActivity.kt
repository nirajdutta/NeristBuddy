package com.diplomaproject.neristbuddy

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
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
        val sharedPreferences=getSharedPreferences(getString(R.string.saved_preferences), MODE_PRIVATE)
        if (sharedPreferences.getBoolean("isLoggedIn",false)){
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnlogin = findViewById(R.id.btnLogin)
        etId = findViewById(R.id.etId)
        etPassword = findViewById(R.id.etPassword)

        btnlogin.setOnClickListener {
            if (etId.text.toString()=="abc"&& etPassword.text.toString().equals("12345")){
                val intent=Intent(this,MainActivity::class.java)
                sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
                startActivity(intent)
                finish()
            }
        }

    }
}