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
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.diplomaproject.neristbuddy.R
import com.google.firebase.FirebaseException
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
    lateinit var edit:TextView

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
        edit=view.findViewById(R.id.edit)

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




        val url=sharedPreferences?.getString("image","R.drawable.user")
        Picasso.get().load(url).placeholder(R.drawable.user).error(R.drawable.user).into(imgProfile)

        imgProfile.setOnClickListener {
            val dialog=AlertDialog.Builder(activity as Context)
            dialog.setMessage("Do you want to change your profile picture?")
            dialog.setPositiveButton("Change",DialogInterface.OnClickListener { dialogInterface, i ->
                val galleryIntent = Intent()
                galleryIntent.action = Intent.ACTION_GET_CONTENT
                galleryIntent.type = "image/*"
                startActivityForResult(
                    galleryIntent,
                    GalleryPickRequest
                )
            })
            dialog.setNegativeButton("Remove",DialogInterface.OnClickListener{dialogInterface, i ->
                try {
                    val loading=ProgressDialog(activity as Context)
                    loading.setMessage("Removing")
                    loading.setCanceledOnTouchOutside(false)
                    loading.show()
                    imageStorageRef.delete().addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(activity as Context, "Profile picture removed Successfully", Toast.LENGTH_SHORT).show()
                            Picasso.get().load(R.drawable.user).into(imgProfile)
                            dbRef.child("Users").child(uid).child("image").removeValue()
                            sharedPreferences?.edit()?.remove("image")?.commit()
                            loading.dismiss()
                        }
                        else{
                            loading.dismiss()
                        }
                    }

                }catch (e:FirebaseException){
                    Toast.makeText(activity,"Failed to remove your picture" ,Toast.LENGTH_SHORT).show()
                    Log.d("Deletion error",e.message.toString())
                }
            })
            dialog.setNeutralButton("Cancel",DialogInterface.OnClickListener { dialogInterface, i ->

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
                        sharedPreferences?.edit()?.putString("image",imgUrl)?.commit()
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

        edit.setOnClickListener {
            val alertDialog=AlertDialog.Builder(activity as Context)
            alertDialog.setTitle("Update your name")
            alertDialog.setMessage("Enter your new username")
            val input=EditText(context)
            input.inputType=InputType.TYPE_TEXT_FLAG_CAP_WORDS
            input.setText(userName)
            alertDialog.setView(input)
            alertDialog.setPositiveButton("Update",DialogInterface.OnClickListener{dialogInterface, i ->
                val loadingBar=ProgressDialog(context)
                loadingBar.setMessage("Updating your name")
                loadingBar.setCanceledOnTouchOutside(false)
                loadingBar.show()
                dbRef.child("Users").child(uid).child("name").setValue(input.text.toString()).addOnSuccessListener {
                    txtName.text=input.text.toString()
                    sharedPreferences?.edit()?.putString("userName",input.text.toString())?.commit()
                    loadingBar.dismiss()
                }
            })
            alertDialog.setNegativeButton("Cancel",DialogInterface.OnClickListener { dialogInterface, i ->  })
            alertDialog.show()
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