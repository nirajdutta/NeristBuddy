package com.diplomaproject.neristbuddy.activities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.diplomaproject.neristbuddy.R
import com.diplomaproject.neristbuddy.util.NotesList
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*

class UploadNotesActivity : AppCompatActivity() {
    lateinit var btnImageSelect: Button
    final val GalleryPickRequest = 1
    lateinit var imgNotes: ImageView
    lateinit var btnUpload: Button
    lateinit var year: String
    lateinit var branch: String
    lateinit var etTopicName: EditText
    lateinit var etNotesDetail: EditText
    lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_notes)
        title = "Upload Notes"

        year = intent.getStringExtra("year").toString()
        branch = intent.getStringExtra("branch").toString()

        imgNotes = findViewById(R.id.imgNotes)
        btnImageSelect = findViewById(R.id.btnImageSelect)
        btnUpload = findViewById(R.id.btnUpload)
        etTopicName = findViewById(R.id.etTopic)
        etNotesDetail = findViewById(R.id.etNotes)

        var db = FirebaseDatabase.getInstance()
        var dbRef = db.reference


        val sharedPreferences = getSharedPreferences(
            R.string.saved_preferences.toString(),
            MODE_PRIVATE
        )

        val userName = sharedPreferences.getString("userName", "Username")

        val imageStorageRef = FirebaseStorage.getInstance().getReference().child("images")
            .child("${Calendar.getInstance().time}.jpg")


        btnImageSelect.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            startActivityForResult(
                galleryIntent,
                GalleryPickRequest
            )
        }
        btnUpload.setOnClickListener {
            if (isValidInput()){
                val loadingBar = ProgressDialog(this)
                loadingBar.setMessage("Please wait, Image is uploading...")
                loadingBar.setCanceledOnTouchOutside(false)
                loadingBar.show()
                imageStorageRef.putFile(imageUri).addOnCompleteListener {
                    imageStorageRef.downloadUrl.addOnCompleteListener {
                        val imgUrl = it.result.toString()
                        println("Image---->$imgUrl")
                        val newNotes = NotesList(
                            etTopicName.text.toString(),
                            etNotesDetail.text.toString(),
                            imgUrl,
                            userName.toString()
                        )
                        dbRef.child("Notes").child(year).child(branch).child(newNotes.name).setValue(
                            newNotes
                        ).addOnCompleteListener {

                            Toast.makeText(this, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                            loadingBar.dismiss()
                            gotoViewNotes()
                        }
                    }
                    loadingBar.dismiss()
                }

            }

        }

    }

    private fun isValidInput(): Boolean {
        val topic:String= etTopicName.text.toString()
        val details:String= etNotesDetail.text.toString()
        var valid = true
        if (TextUtils.isEmpty(topic.trim())) {
            etTopicName.error = "Required"
            valid = false
        }

        if (TextUtils.isEmpty(details.trim())) {
            etNotesDetail.error = "Required"
            valid = false
        }
        return valid
    }

    fun gotoViewNotes() {
        val intent = Intent(this@UploadNotesActivity, ViewNotesActivity::class.java)
        intent.putExtra("year", year)
        intent.putExtra("branch", branch)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GalleryPickRequest && resultCode == RESULT_OK) {
            imgNotes.visibility = View.VISIBLE
            val selectImageUri: Uri? = data?.data
            if (selectImageUri != null) {
                imageUri = selectImageUri
            }
            Picasso.get().load(selectImageUri).into(imgNotes)
            btnUpload.visibility = View.VISIBLE


        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        gotoViewNotes()
    }
}