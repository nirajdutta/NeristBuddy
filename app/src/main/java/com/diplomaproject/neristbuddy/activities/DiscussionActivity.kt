package com.diplomaproject.neristbuddy.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diplomaproject.neristbuddy.R
import com.diplomaproject.neristbuddy.adapter.MessageRecyclerAdapter
import com.diplomaproject.neristbuddy.model.MessageModel
import com.diplomaproject.neristbuddy.util.ConnectionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class DiscussionActivity : AppCompatActivity() {

    lateinit var txtDQuestion:TextView
    lateinit var messageRecyclerAdapter: MessageRecyclerAdapter
    lateinit var sendButton:ImageView
    lateinit var inputMessage:EditText
    lateinit var recyclerView: RecyclerView
    lateinit var attachment:ImageView
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var imgAttached:ImageView

    var select:String?=null

    lateinit var imageName: String
    lateinit var imageUri: Uri
    val messageList= arrayListOf<MessageModel>()
    val uid=FirebaseAuth.getInstance().uid.toString()
    val dbRef=FirebaseDatabase.getInstance().reference

    @SuppressLint("SimpleDateFormat", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discussion)
        title="Doubt Discussion"
        val question:String=intent.getStringExtra("doubt").toString()
        val doubtKey:String=intent.getStringExtra("key").toString()
        txtDQuestion=findViewById(R.id.txtDQuestion)
        sendButton=findViewById(R.id.send_message_button)
        inputMessage=findViewById(R.id.input_chat_message)
        recyclerView=findViewById(R.id.chatRecyclerView)
        attachment=findViewById(R.id.select_chat_attachment)
        imgAttached=findViewById(R.id.imgAttached)



        val sharedPreferences=getSharedPreferences(R.string.saved_preferences.toString(), MODE_PRIVATE)
        val username=sharedPreferences.getString("userName",uid)

        linearLayoutManager= LinearLayoutManager(this)

        txtDQuestion.text=question

        attachment.setOnClickListener {
            val alertDialog=AlertDialog.Builder(this)
            alertDialog.setTitle("Select Image")
            alertDialog.setMessage("Select from gallery")
            alertDialog.setPositiveButton("Yes",DialogInterface.OnClickListener { dialogInterface, i ->
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        100)

                } else if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    galleryIntent()
                }

            })

            alertDialog.setNegativeButton("Cancel",DialogInterface.OnClickListener { dialogInterface, i ->})
            alertDialog.create()
            alertDialog.show()

        }

        if (ConnectionManager().checkConnectivity(this)){

            dbRef.child("Discussion").child(doubtKey).addChildEventListener(object :ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.hasChild("image")){
                        val newMessage=MessageModel(
                                snapshot.child("name").value as String,
                                null,
                                snapshot.child("time").value as String,
                                snapshot.child("image").value as String,
                                snapshot.child("msgKey").value as String,
                                snapshot.child("uid").value as String
                        )
                        messageList.add(newMessage)
                    }
                    else{
                        val newMessage=MessageModel(
                                snapshot.child("name").value as String,
                                snapshot.child("message").value as String,
                                snapshot.child("time").value as String,
                                null,
                                snapshot.child("msgKey").value as String,
                                snapshot.child("uid").value as String
                        )
                        messageList.add(newMessage)
                    }
                    messageRecyclerAdapter= MessageRecyclerAdapter(this@DiscussionActivity,messageList,doubtKey)
                    messageRecyclerAdapter.notifyDataSetChanged()
                    recyclerView.layoutManager=linearLayoutManager
                    recyclerView.adapter=messageRecyclerAdapter
//                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter()!!.getItemCount())
                    recyclerView.layoutManager?.scrollToPosition(messageList.size-1)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    if (snapshot.hasChild("image")){
                        val removed=MessageModel(
                                snapshot.child("name").value as String,
                                null,
                                snapshot.child("time").value as String,
                                snapshot.child("image").value as String,
                                snapshot.child("msgKey").value as String,
                                snapshot.child("uid").value as String
                        )
                        messageList.remove(removed)
                    }
                    else{
                        val removed=MessageModel(
                                snapshot.child("name").value as String,
                                snapshot.child("message").value as String,
                                snapshot.child("time").value as String,
                                null,
                                snapshot.child("msgKey").value as String,
                                snapshot.child("uid").value as String
                        )
                        messageList.remove(removed)
                    }

                    messageRecyclerAdapter.notifyDataSetChanged()
//                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter()!!.getItemCount())

                    recyclerView.layoutManager?.scrollToPosition(messageList.size-1)
                    if(messageList.isEmpty()){
                        startActivity(Intent(this@DiscussionActivity,DoubtsActivity::class.java))
                        Toast.makeText(this@DiscussionActivity,"This discussion has been removed by the user",Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


        }


        sendButton.setOnClickListener {
            val strMsg:String=inputMessage.text.toString()
            if (strMsg.isNotEmpty()&&select==null){
                val sdf= SimpleDateFormat("dd/MM hh:mm aa")
                val time=sdf.format(Date())
                val msgKey=dbRef.child("Discussion").child(doubtKey).push().key.toString()


                    val newMessage=MessageModel(
                            username.toString(),
                            strMsg,
                            time,
                            null,
                            msgKey,
                            uid
                    )

                    dbRef.child("Discussion").child(doubtKey).child(msgKey).setValue(newMessage).addOnCompleteListener {
//                        messageRecyclerAdapter.notifyDataSetChanged()
                        inputMessage.setText("")
                    }

                select=null
            }
            if (select=="image"){
                val sdf= SimpleDateFormat("dd/MM hh:mm aa")
                val time=sdf.format(Date())
                val msgKey=dbRef.child("Discussion").child(doubtKey).push().key.toString()
                val loadingBar = ProgressDialog(this)
                loadingBar.setMessage("Please wait, Image is uploading...")
                loadingBar.setCanceledOnTouchOutside(false)
                loadingBar.show()
                val imageStorageRef = FirebaseStorage.getInstance().reference.child("images")
                        .child(imageName)
                imageStorageRef.putFile(imageUri).addOnCompleteListener {
                    imageStorageRef.downloadUrl.addOnCompleteListener {
                        val imgUrl = it.result.toString()

//                            val msgKey=dbRef.child("Discussion").child(doubtKey).push().key.toString()
                        val newPost=MessageModel(
                                username.toString(),
                                null,
                                time,
                                imgUrl,
                                msgKey,
                                uid
                        )
                        dbRef.child("Discussion").child(doubtKey).child(msgKey).setValue(newPost).addOnCompleteListener {
                            imgAttached.visibility=View.GONE
                            messageRecyclerAdapter.notifyDataSetChanged()
//                                llsend.visibility=View.GONE
                            select=null
                            loadingBar.dismiss()
                        }
                    }
                }

            }

        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            galleryIntent()
        }
        else{
            Toast.makeText(this, "Please allow to continue", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode== RESULT_OK&&requestCode==10){

            imgAttached.visibility = View.VISIBLE
            imgAttached.bringToFront()
//            llsend.visibility=View.VISIBLE
            data?.data?.let { returnUri ->
                contentResolver.query(returnUri, null, null, null, null)?.use { cursor ->
                    imageUri = returnUri
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    val result = cursor.getString(nameIndex)
                    imageName = result
                    select="image"
                }
            }
            Picasso.get().load(imageUri).into(imgAttached)

        }

    }
    fun galleryIntent(){
        val galleryIntent = Intent()
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        galleryIntent.type = "image/*"
        startActivityForResult(
            galleryIntent,
            10
        )
    }

    override fun onBackPressed() {
        if (imgAttached.visibility==View.VISIBLE){
            imgAttached.visibility = View.GONE
            select=null
        }

        else{
            super.onBackPressed()
        }

    }
}