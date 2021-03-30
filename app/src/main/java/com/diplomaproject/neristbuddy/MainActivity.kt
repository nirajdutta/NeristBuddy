package com.diplomaproject.neristbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    lateinit var btnNotes:Button
    lateinit var btnDoubts:Button
    lateinit var btnTnp:Button
    lateinit var btnLost:Button
    lateinit var btnEvents:Button
    lateinit var btnCCA:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnNotes=findViewById(R.id.btnNotes)
        btnDoubts=findViewById(R.id.btnDoubts)
        btnTnp=findViewById(R.id.btnTnp)
        btnLost=findViewById(R.id.btnLost)
        btnEvents=findViewById(R.id.btnEvents)
        btnCCA=findViewById(R.id.btnCCA)

        btnNotes.setOnClickListener {
            startActivity(Intent(this,NotesActivity::class.java))
        }

    }
}