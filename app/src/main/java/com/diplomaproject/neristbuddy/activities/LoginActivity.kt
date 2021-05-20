package com.diplomaproject.neristbuddy.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.diplomaproject.neristbuddy.R
import com.diplomaproject.neristbuddy.util.ConnectionManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {
    lateinit var btnlogin: Button
    lateinit var etId: TextInputEditText
    lateinit var etPassword: TextInputEditText
    lateinit var llLogin:RelativeLayout
    lateinit var rlProgress:RelativeLayout
    lateinit var txtRegister:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
//        val sharedPreferences=getSharedPreferences(getString(R.string.saved_preferences), MODE_PRIVATE)
//        if (sharedPreferences.getBoolean("isLoggedIn",false)){
//            val intent=Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
        llLogin=findViewById(R.id.llLogin)
        btnlogin = findViewById(R.id.btnLogin)
        etId = findViewById(R.id.etId)
        etPassword = findViewById(R.id.etPassword)
        rlProgress=findViewById(R.id.rlProgress)
        txtRegister=findViewById(R.id.txtRegister)

        llLogin.setOnClickListener {
            hideKeyBoard()
        }
        val auth=FirebaseAuth.getInstance()
        var user=auth.currentUser

        txtRegister.paintFlags = txtRegister.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        txtRegister.setOnClickListener {
            startActivity(Intent(this,RegistrationActivity::class.java))
        }

        btnlogin.setOnClickListener {

            if (isValidInput()){
                val loading= ProgressDialog(this)
                loading.setMessage("Please wait Signing in...")
                loading.setCanceledOnTouchOutside(false)
                loading.show()
                if (ConnectionManager().checkConnectivity(this)){
                    auth.signInWithEmailAndPassword(etId.text.toString(), etPassword.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful){
                            user = auth.currentUser!!
                            val uid= user!!.uid
                            Toast.makeText(this, "Authentication Successful", Toast.LENGTH_SHORT).show()
                            val intent=Intent(this, MainActivity::class.java)
//                    sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
                            startActivity(intent)
                            loading.dismiss()
                            finish()
                        }
                        else{
                            loading.dismiss()
                            Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                            val msg=it.exception.toString()
                            println("error:  $msg")
                            if (msg.contains("badly formatted", true)){
                                Toast.makeText(
                                    this,
                                    "Enter a correct email address",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else if (msg.contains("no user record", true)){
                                Toast.makeText(
                                    this,
                                    "No user found on this email id",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else if (msg.contains("password is invalid", true)){
                                Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show()
                            }
                            else if (msg.contains("We have blocked", true)){
                                Toast.makeText(
                                    this, "Access to this account has been temporarily disabled " +
                                            "due to many failed login attempts." +
                                            " You can immediately restore it by resetting your " +
                                            "password or you can try again later", Toast.LENGTH_SHORT
                                ).show()
                            }
                            else{
                                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                }
                else{
                    loading.dismiss()
                    Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show()
                }

            }
//            loading.dismiss()

        }




    }
    private fun hideKeyBoard(){
        val view=this.currentFocus
        if (view!=null){
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
    private fun isValidInput(): Boolean {
        val email:String= etId.text.toString()
        val password:String= etPassword.text.toString()
        var valid = true
        if (TextUtils.isEmpty(email.trim())) {
            etId.error = "Required"
            valid = false
        }
        if (!TextUtils.isEmpty(email.trim())&&!email.trim().contains("@")){
            etId.error = "Invalid email"
            valid = false
        }
        if (TextUtils.isEmpty(password.trim())) {
            etPassword.error = "Required"
            valid = false
        }
        return valid
    }
}