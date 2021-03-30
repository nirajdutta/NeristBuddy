package com.diplomaproject.neristbuddy.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

import com.diplomaproject.neristbuddy.R
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val auth=FirebaseAuth.getInstance()
        val user=auth.currentUser
        var handler=Handler()
        handler = Handler()
        handler.postDelayed({
            if (user==null){
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }
            else{
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        },3000)
    }

    override fun onStart() {
        super.onStart()
    }
}