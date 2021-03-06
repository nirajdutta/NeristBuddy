package com.diplomaproject.neristbuddy.activities


import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.diplomaproject.neristbuddy.R
import com.diplomaproject.neristbuddy.fragments.ContactUsFragment
import com.diplomaproject.neristbuddy.fragments.HomeFragment
import com.diplomaproject.neristbuddy.fragments.ProfileFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File


class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var navigationView: NavigationView
    lateinit var txtUsername:TextView
    lateinit var headerLayout: View
    lateinit var imgProfile:CircleImageView

    val profileFragment=ProfileFragment()
    val contactUsFragment=ContactUsFragment()
    var previousMenuItem: MenuItem? = null
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val db=FirebaseDatabase.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.dlMain)
        toolbar = findViewById(R.id.toolbar)

        val sharedPreferences=getSharedPreferences(R.string.saved_preferences.toString(),
            MODE_PRIVATE)

        navigationView = findViewById(R.id.navigationView)
        headerLayout=navigationView.inflateHeaderView(R.layout.headerlayout)
        imgProfile=headerLayout.findViewById(R.id.imgUser)
        txtUsername=headerLayout.findViewById(R.id.txtUsername)
        val uid=user?.uid

        setupToolbar()
        openHome()


        var userName:String
        val ref=db.reference.child("Users").child(uid!!)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userName=snapshot.child("name").value.toString()
                txtUsername.text=userName
//                println(userName)
                sharedPreferences.edit().putString("userName",userName).commit()
                sharedPreferences.edit().putString("email",snapshot.child("email").value.toString()).apply()
                if (snapshot.hasChild("image")){
                    sharedPreferences.edit().putString("image",snapshot.child("image").value.toString()).apply()
                    Picasso.get().load(snapshot.child("image").value.toString()).placeholder(R.drawable.user).error(R.drawable.user).into(imgProfile)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("dbError", error.message)
            }

        })



        imgProfile.setOnClickListener {
            val ft=supportFragmentManager.beginTransaction()
            ft.replace(R.id.frameLayout,profileFragment)
            supportActionBar?.title="Profile"
            ft.commit()
            drawerLayout.closeDrawers()
        }

        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }


            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when (it.itemId) {
                R.id.homepage->openHome()


                R.id.logout -> {

                    val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
                    dialog.setTitle("Log Out")
                    dialog.setMessage("Do you want to Log out?")
                    dialog.setPositiveButton("Yes",
                        DialogInterface.OnClickListener { dialogInterface, d ->
                            auth.signOut()
                            val context: Context = applicationContext
                            val intent = Intent(context, LoginActivity::class.java)
                            startActivity(intent)
                            sharedPreferences.edit().clear().apply()
                            finish()
                        })
                        .setNegativeButton(
                            "No",
                            DialogInterface.OnClickListener { dialogInterface, d -> })
                    dialog.create()
                    dialog.show()
                    it.isCheckable = false
                    it.isChecked = false
                    drawerLayout.closeDrawers()
                }

                R.id.profile->{

                    val ft=supportFragmentManager.beginTransaction()
                    ft.replace(R.id.frameLayout,profileFragment)
                    supportActionBar?.title="Profile"
                    ft.commit()
                    drawerLayout.closeDrawers()
                }
                R.id.contact->{

                    val ft=supportFragmentManager.beginTransaction()
                    ft.replace(R.id.frameLayout,contactUsFragment)
                    supportActionBar?.title="Developers Contact"
                    ft.commit()
                    drawerLayout.closeDrawers()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    override fun onStart() {
        super.onStart()
        var userName:String
        val sharedPreferences=getSharedPreferences(R.string.saved_preferences.toString(),
            MODE_PRIVATE
        )
        val uid=FirebaseAuth.getInstance().uid.toString()
        val ref=db.reference.child("Users").child(uid)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userName=snapshot.child("name").value.toString()
                txtUsername.text=userName
//                println(userName)
                sharedPreferences.edit().putString("userName",userName).commit()
                sharedPreferences.edit().putString("email",snapshot.child("email").value.toString()).apply()
                if (snapshot.hasChild("image")){
                    sharedPreferences.edit().putString("image",snapshot.child("image").value.toString()).apply()
                    Picasso.get().load(snapshot.child("image").value.toString()).placeholder(R.drawable.user).error(R.drawable.user).into(imgProfile)
                }else{
                    Picasso.get().load(R.drawable.user).into(imgProfile)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("dbError", error.message)
            }

        })
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Nerist Buddy"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_tab)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
            navigationView.bringToFront()
        }
        return super.onOptionsItemSelected(item)
    }
    fun openHome(){
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
        supportActionBar?.title = "Dashboard"
        navigationView.setCheckedItem(R.id.homepage)
        drawerLayout.closeDrawers()
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frameLayout)
        when (frag) {
            !is HomeFragment -> openHome()
            else ->super.onBackPressed()
        }

    }

}