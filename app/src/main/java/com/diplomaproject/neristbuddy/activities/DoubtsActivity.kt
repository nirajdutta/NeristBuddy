package com.diplomaproject.neristbuddy.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diplomaproject.neristbuddy.R
import com.diplomaproject.neristbuddy.adapter.DoubtsRecyclerAdapter
import com.diplomaproject.neristbuddy.model.Doubt
import com.diplomaproject.neristbuddy.util.ConnectionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class DoubtsActivity : AppCompatActivity() {

    lateinit var rlProgress: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var btnAsk: Button
    lateinit var recyclerDoubts: RecyclerView
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var doubtsRecyclerAdapter:DoubtsRecyclerAdapter
    val doubtList= arrayListOf<Doubt>()

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doubts)
        title = "Doubts"

        rlProgress = findViewById(R.id.rlProgress)
        progressBar = findViewById(R.id.progressbar)
        btnAsk = findViewById(R.id.btnAskDoubt)
        recyclerDoubts = findViewById(R.id.recyclerDoubts)

        val uid=FirebaseAuth.getInstance().uid.toString()
        val dbRef=FirebaseDatabase.getInstance().reference
        val sharedPreferences=getSharedPreferences(R.string.saved_preferences.toString(), MODE_PRIVATE)
        var username=sharedPreferences.getString("userName",uid)

        if (username==uid){
            dbRef.child("Users").child(uid).child("name").addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    username=snapshot.value.toString()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
//        rlProgress.visibility = View.GONE
        linearLayoutManager= LinearLayoutManager(this)


        if (ConnectionManager().checkConnectivity(this)){

            dbRef.child("Doubts").addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    doubtList.clear()

                    for (doubt:DataSnapshot in snapshot.children){
                        val newDoubt=Doubt(
                                doubt.child("askedBy").value as String,
                                doubt.child("time").value as String,
                                doubt.child("subject").value as String,
                                doubt.child("doubt").value as String,
                                doubt.child("uid").value as String,
                                doubt.child("key").value as String
                        )
                        doubtList.add(newDoubt)
                        doubtsRecyclerAdapter= DoubtsRecyclerAdapter(this@DoubtsActivity,doubtList)
                        recyclerDoubts.layoutManager=linearLayoutManager
                        doubtsRecyclerAdapter.notifyDataSetChanged()
                        recyclerDoubts.adapter=doubtsRecyclerAdapter

                    }
                    rlProgress.visibility=View.GONE
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DoubtsActivity, "Some unexpected error has occurred", Toast.LENGTH_SHORT).show()
                }

            })

        }




        btnAsk.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Ask a new doubt")
            alertDialog.setMessage("Please enter the following details")

            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.VERTICAL
            val et1 = EditText(this)
            et1.hint = "Please enter the subject name"
            et1.inputType=InputType.TYPE_TEXT_FLAG_CAP_WORDS
            linearLayout.addView(et1)
            val et2 = EditText(this)
            et2.hint = "Please enter your doubt"
            et2.inputType=InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            et2.isSingleLine=false
            linearLayout.addView(et2)
            alertDialog.setView(linearLayout)

            alertDialog.setPositiveButton("Post",
                    DialogInterface.OnClickListener { dialogInterface, i ->

                        if (et1.text.isNotEmpty()&&et2.text.isNotEmpty()){
                            val loadingBar = ProgressDialog(this)
                            loadingBar.setMessage("Updating...")
                            loadingBar.setCanceledOnTouchOutside(false)
                            loadingBar.show()
                            val sdf=SimpleDateFormat("dd/MM/yyyy hh:mm aa")
                            val time=sdf.format(Date())
                            println(time)

                            val doubtkey=dbRef.child("Doubts").push().key
                            val doubt=Doubt(
                                    username.toString(),
                                    time,
                                    et1.text.toString(),
                                    et2.text.toString(),
                                    uid,
                                    doubtkey.toString()
                            )
                            dbRef.child("Doubts").child(doubtkey.toString()).setValue(doubt).addOnCompleteListener {
                                loadingBar.dismiss()
                                doubtsRecyclerAdapter.notifyDataSetChanged()
                                Toast.makeText(this, "Doubt Successfully posted", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else{

                            Toast.makeText(this, "Do not leave the fields empty", Toast.LENGTH_SHORT).show()
                        }

                    })
            alertDialog.setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialogInterface, i -> })
            alertDialog.show()
        }

    }
}