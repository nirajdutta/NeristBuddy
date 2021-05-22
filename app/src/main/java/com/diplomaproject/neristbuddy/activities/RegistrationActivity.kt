package com.diplomaproject.neristbuddy.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import com.diplomaproject.neristbuddy.R
import com.diplomaproject.neristbuddy.model.User
import com.diplomaproject.neristbuddy.util.ConnectionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistrationActivity : AppCompatActivity() {
    lateinit var etEmail:EditText
    lateinit var etName:EditText
    lateinit var etPassword:EditText
    lateinit var btnRegister:Button
    lateinit var rlRegister:RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        title="Register Yourself"

        etEmail=findViewById(R.id.etEmailId)
        etName=findViewById(R.id.etUserName)
        etPassword=findViewById(R.id.etPassword)
        btnRegister=findViewById(R.id.btnRegister)
        rlRegister=findViewById(R.id.rlRegister)

        val auth= FirebaseAuth.getInstance()

        rlRegister.setOnClickListener {
            hideKeyBoard()
        }

        btnRegister.setOnClickListener {

            if (isValidInput()){
                val loadingBar=ProgressDialog(this)
                loadingBar.setMessage("Please wait. Creating your account")
                loadingBar.setCanceledOnTouchOutside(false)
                loadingBar.show()
                if (ConnectionManager().checkConnectivity(this)){
                    auth.createUserWithEmailAndPassword(etEmail.text.toString().trim(),etPassword.text.toString().trim()).addOnCompleteListener {
                        if (it.isSuccessful){
                            val user=auth.currentUser
                            val uid= user?.uid.toString()
                            val dbRef=FirebaseDatabase.getInstance().reference
                            val newUser= User(etEmail.text.toString().trim(),etName.text.toString(),uid)
                            dbRef.child("Users").child(uid).setValue(newUser).addOnCompleteListener {
                                loadingBar.dismiss()
                                Toast.makeText(this,"Registration Successful",Toast.LENGTH_SHORT).show()
                                auth.signOut()
                                startActivity(Intent(this@RegistrationActivity,LoginActivity::class.java))
                                finish()
                            }
                        }
                        else{
                            loadingBar.dismiss()
//                        Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
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
                            else if (msg.contains("The email address is already in use by another account",true)){
                                Toast.makeText(this, "The email address is already in use by another account", Toast.LENGTH_SHORT).show()
                            }
                            else{
                                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }else{
                    loadingBar.dismiss()
                    Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show()
                }

            }
        }

    }

    private fun isValidInput(): Boolean {
        val email:String= etEmail.text.toString()
        val password:String= etPassword.text.toString()
        val name:String=etName.text.toString()
        var valid = true
        if (TextUtils.isEmpty(email.trim())) {
            etEmail.error = "Required"
            valid = false
        }
        if (!TextUtils.isEmpty(email.trim())&&!email.trim().contains("@")&&!email.trim().contains(".com")){
            etEmail.error = "Invalid email"
            valid = false
        }
        if (TextUtils.isEmpty(name.trim())) {
            etName.error = "Required"
            valid = false
        }
        if (TextUtils.isEmpty(password.trim())) {
            etPassword.error = "Required"
            valid = false
        }
        return valid
    }
    private fun hideKeyBoard(){
        val view=this.currentFocus
        if (view!=null){
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}