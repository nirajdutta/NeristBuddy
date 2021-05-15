package com.diplomaproject.neristbuddy.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.diplomaproject.neristbuddy.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class ProfileFragment : Fragment() {

    val GalleryPickRequest=1
    lateinit var txtName:TextView
    lateinit var txtEmail:TextView
    lateinit var imgProfile:CircleImageView
    lateinit var imageUri: Uri
    lateinit var btnUpload:Button
    lateinit var userName:String
    lateinit var userEmail:String
    lateinit var btnCancel:Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =inflater.inflate(R.layout.fragment_profile, container, false)

        txtName=view.findViewById(R.id.txtUsername)
        txtEmail=view.findViewById(R.id.txtEmail)
        imgProfile=view.findViewById(R.id.profile_image)
        btnUpload=view.findViewById(R.id.btnUpdate)
        btnCancel=view.findViewById(R.id.btnCancel)

        val sharedPreferences=activity?.getSharedPreferences(R.string.saved_preferences.toString(),
            MODE_PRIVATE
        )

        val auth=FirebaseAuth.getInstance()
        val dbRef=FirebaseDatabase.getInstance().reference
        val uid= auth.currentUser?.uid.toString()

        val imageStorageRef = FirebaseStorage.getInstance().reference.child("profile")
            .child("${uid}.jpg")



        dbRef.child("Users").child(uid).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userName=snapshot.child("name").value.toString()
                userEmail=snapshot.child("email").value.toString()
                txtName.text=userName
                txtEmail.text=userEmail
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })




        val url=sharedPreferences?.getString("image","https://firebasestorage.googleapis.com/v0/b/my-nerist-buddy.appspot.com/o/profile%2Fuser.png?alt=media&token=48d72495-c2d7-497e-b4e7-24dd40331632")
        Picasso.get().load(url).placeholder(R.drawable.user).error(R.drawable.user).into(imgProfile)

        imgProfile.setOnClickListener {
            val dialog=AlertDialog.Builder(activity as Context)
            dialog.setMessage("Do you want to change your profile picture?")
            dialog.setPositiveButton("Yes",DialogInterface.OnClickListener { dialogInterface, i ->
                val galleryIntent = Intent()
                galleryIntent.action = Intent.ACTION_GET_CONTENT
                galleryIntent.type = "image/*"
                startActivityForResult(
                    galleryIntent,
                    GalleryPickRequest
                )
            })
            dialog.setNegativeButton("No",DialogInterface.OnClickListener { dialogInterface, i ->

            })
            dialog.create()
            dialog.show()
        }

        btnUpload.setOnClickListener {
            val loading=ProgressDialog(activity as Context)
            loading.setMessage("Uploading...")
            loading.setCanceledOnTouchOutside(false)
            loading.show()
            imageStorageRef.putFile(imageUri).addOnCompleteListener {
                imageStorageRef.downloadUrl.addOnCompleteListener {
                    val imgUrl = it.result.toString()

                    dbRef.child("Users").child(uid).child("image").setValue(imgUrl).addOnCompleteListener {
                        Toast.makeText(activity as Context, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                        sharedPreferences?.edit()?.putString("image",imgUrl)?.apply()
                        btnUpload.visibility=View.GONE
                        btnCancel.visibility=View.GONE
                        loading.dismiss()
                    }
                }
                loading.dismiss()
            }
        }
        btnCancel.setOnClickListener {
            val dialog=AlertDialog.Builder(activity as Context)
            dialog.setMessage("Do you want to cancel?")
            dialog.setPositiveButton("Yes",DialogInterface.OnClickListener{dialogInterface, i ->
                Picasso.get().load(url).placeholder(R.drawable.user).error(R.drawable.user).into(imgProfile)
                btnUpload.visibility=View.GONE
                btnCancel.visibility=View.GONE
            })
            dialog.setNegativeButton("No",DialogInterface.OnClickListener{dialogInterface, i ->

            })
            dialog.create()
            dialog.show()

        }

        return view
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GalleryPickRequest && resultCode == AppCompatActivity.RESULT_OK) {
            val selectImageUri: Uri? = data?.data
            if (selectImageUri != null) {
                imageUri = selectImageUri
            }
            Picasso.get().load(imageUri).into(imgProfile)
            btnUpload.visibility=View.VISIBLE
            btnCancel.visibility=View.VISIBLE
        }
    }

}