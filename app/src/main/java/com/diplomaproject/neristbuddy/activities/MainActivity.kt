package com.diplomaproject.neristbuddy.activities


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.diplomaproject.neristbuddy.R
import com.diplomaproject.neristbuddy.fragments.ProfileFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {
    lateinit var btnNotes: Button
    lateinit var btnDoubts: Button
    lateinit var btnTnp: Button
    lateinit var btnLost: Button
    lateinit var btnEvents: Button
    lateinit var btnCCA: Button
    lateinit var drawerLayout: DrawerLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var navigationView: NavigationView
    lateinit var txtUsername:TextView
    lateinit var headerLayout: View

    val profileFragment=ProfileFragment()
    var previousMenuItem: MenuItem? = null
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val db=FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.dlMain)
        toolbar = findViewById(R.id.toolbar)
        btnNotes = findViewById(R.id.btnNotes)
        btnDoubts = findViewById(R.id.btnDoubts)
        btnTnp = findViewById(R.id.btnTnp)
        btnLost = findViewById(R.id.btnLost)
        btnEvents = findViewById(R.id.btnEvents)
        btnCCA = findViewById(R.id.btnCCA)
        navigationView = findViewById(R.id.navigationView)
        headerLayout=navigationView.inflateHeaderView(R.layout.headerlayout)

        txtUsername=headerLayout.findViewById(R.id.txtUsername)
        val uid=user?.uid

        var userName:String
        val ref=db.reference.child("Users").child(uid!!)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userName=snapshot.child("name").value.toString()
                txtUsername.text=userName
                println(userName)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("dbError", error.message)
            }

        })





        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Nerist Buddy"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_tab)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        btnNotes.setOnClickListener {
            startActivity(Intent(this, NotesActivity::class.java))
        }


        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }


            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when (it.itemId) {
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
            }
            return@setNavigationItemSelectedListener true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
            navigationView.bringToFront()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {


        if (supportActionBar?.title!="My Nerist Buddy"){
            supportFragmentManager.beginTransaction().detach(profileFragment).commit()
            navigationView.checkedItem!!.isChecked=false
            navigationView.checkedItem!!.isCheckable=false
            supportActionBar?.title="My Nerist Buddy"
        }
        else{
            drawerLayout.closeDrawers()
            super.onBackPressed()
        }

    }
}