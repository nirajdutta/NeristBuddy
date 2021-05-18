package com.diplomaproject.neristbuddy.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager.*
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.diplomaproject.neristbuddy.R
import com.diplomaproject.neristbuddy.util.NotesList
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.text.CharacterIterator
import java.text.StringCharacterIterator
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
    lateinit var pdfUri: Uri
    lateinit var cardView: CardView
    lateinit var txtPdfName: TextView
    lateinit var scrollView: ScrollView

    lateinit var imageName: String
    lateinit var pdfName: String
    var select: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_notes)
        title = "Upload Notes"

        year = intent.getStringExtra("year").toString()
        branch = intent.getStringExtra("branch").toString()

        scrollView=findViewById(R.id.layoutScrollView)
        imgNotes = findViewById(R.id.imgNotes)
        btnImageSelect = findViewById(R.id.btnImageSelect)
        btnUpload = findViewById(R.id.btnUpload)
        etTopicName = findViewById(R.id.etTopic)
        etNotesDetail = findViewById(R.id.etNotes)
        cardView = findViewById(R.id.pdfView)
        txtPdfName = findViewById(R.id.pdfName)

        btnUpload.visibility = View.VISIBLE
        var db = FirebaseDatabase.getInstance()
        var dbRef = db.reference

        etTopicName.onFocusChangeListener = View.OnFocusChangeListener { p0, p1 -> hideKeyBoard() }
        etNotesDetail.onFocusChangeListener= View.OnFocusChangeListener { p0, p1 -> hideKeyBoard() }

        val sharedPreferences = getSharedPreferences(
                R.string.saved_preferences.toString(),
                MODE_PRIVATE
        )

        val userName = sharedPreferences.getString("userName", "Username")




        btnImageSelect.setOnClickListener {


            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        100)

            } else if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PERMISSION_GRANTED) {
                showDialog()
            }


        }
        btnUpload.setOnClickListener {
            if (isValidInput()) {
                if (select == null) {
                    val loadingBar = ProgressDialog(this)
                    loadingBar.setMessage("Please wait, Notes is uploading...")
                    loadingBar.setCanceledOnTouchOutside(false)
                    loadingBar.show()
                    val newNotes = NotesList(
                            etTopicName.text.toString(),
                            etNotesDetail.text.toString(),
                            null,
                            null,
                            null,
                            null,
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

                if (select == "image") {
                    val loadingBar = ProgressDialog(this)
                    loadingBar.setMessage("Please wait, Image is uploading...")
                    loadingBar.setCanceledOnTouchOutside(false)
                    loadingBar.show()
                    val imageStorageRef = FirebaseStorage.getInstance().reference.child("images")
                            .child(imageName)
                    imageStorageRef.putFile(imageUri).addOnCompleteListener {
                        imageStorageRef.downloadUrl.addOnCompleteListener {
                            val imgUrl = it.result.toString()
                            println("Image---->$imgUrl")
                            val newNotes = NotesList(
                                    etTopicName.text.toString(),
                                    etNotesDetail.text.toString(),
                                    imgUrl,
                                    imageName,
                                    null,
                                    null,
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
                } else if (select == "pdf") {
                    val loadingBar = ProgressDialog(this)
                    loadingBar.setMessage("Please wait, pdf is uploading...")
                    loadingBar.setCanceledOnTouchOutside(false)
                    loadingBar.show()
                    val pdfStorageRef = FirebaseStorage.getInstance().reference.child("pdf")
                            .child(pdfName)
                    val uploadTask = pdfStorageRef.putFile(pdfUri)
                    uploadTask.addOnProgressListener {
                        loadingBar.setMessage("Please wait, pdf is uploading...\n(${humanReadableByteCountBin(it.bytesTransferred)}/${humanReadableByteCountBin(it.totalByteCount)})")
                    }
                    uploadTask.addOnCompleteListener {
                        loadingBar.setMessage("Please wait...")

                        pdfStorageRef.downloadUrl.addOnCompleteListener {
                            val pdfUrl = it.result.toString()
                            println("Pdf---->$pdfUrl")
                            val newNotes = NotesList(
                                    etTopicName.text.toString(),
                                    etNotesDetail.text.toString(),
                                    null,
                                    null,
                                    pdfUrl,
                                    pdfName,
                                    userName.toString()
                            )
                            dbRef.child("Notes").child(year).child(branch).child(newNotes.name).setValue(
                                    newNotes
                            ).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(this, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                                    gotoViewNotes()
                                    loadingBar.dismiss()
                                } else {
                                    Toast.makeText(this, "Could not add to database. Please try again", Toast.LENGTH_SHORT).show()
                                }


                            }
                        }
                        loadingBar.dismiss()
                    }

                }


            }

        }

    }

    fun showDialog() {
        select = null
        val option = arrayOf<CharSequence>("Images", "Pdf")
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Select file type")
        builder.setItems(option) { dialog: DialogInterface?, items: Int ->
            if (option[items] === "Images") {
                // select image
                select = "image"

                cardView.visibility = View.GONE
                val galleryIntent = Intent()
                galleryIntent.action = Intent.ACTION_GET_CONTENT
                galleryIntent.type = "image/*"
                startActivityForResult(
                        galleryIntent,
                        GalleryPickRequest
                )
            } else if (option[items] === "Pdf") {
                // select pdf

                select = "pdf"
                imgNotes.visibility = View.GONE
                val galleryIntent = Intent()
                galleryIntent.action = Intent.ACTION_GET_CONTENT
                galleryIntent.type = "application/pdf"
                startActivityForResult(
                        galleryIntent,
                        2
                )
            }

        }
        builder.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults[0] == PERMISSION_GRANTED) {
            showDialog()
        }
    }


    private fun isValidInput(): Boolean {
        val topic: String = etTopicName.text.toString()
        val details: String = etNotesDetail.text.toString()
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

            data?.data?.let { returnUri ->
                contentResolver.query(returnUri, null, null, null, null)?.use { cursor ->
                    imageUri = returnUri
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    val result = cursor.getString(nameIndex)
                    imageName = result

                }
            }
            Picasso.get().load(imageUri).into(imgNotes)
        }
        if (requestCode == 2 && resultCode == RESULT_OK) {
            cardView.visibility = View.VISIBLE


            data?.data?.let { returnUri ->
                contentResolver.query(returnUri, null, null, null, null)?.use { cursor ->
                    pdfUri = returnUri
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    val result = cursor.getString(nameIndex)
                    pdfName = result
                    txtPdfName.text = result

                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    @SuppressLint("DefaultLocale")
    fun humanReadableByteCountBin(bytes: Long): String? {
        val absB = if (bytes == Long.MIN_VALUE) Long.MAX_VALUE else Math.abs(bytes)
        if (absB < 1024) {
            return "$bytes B"
        }
        var value = absB
        val ci: CharacterIterator = StringCharacterIterator("KMGTPE")
        var i = 40
        while (i >= 0 && absB > 0xfffccccccccccccL shr i) {
            value = value shr 10
            ci.next()
            i -= 10
        }
        value *= java.lang.Long.signum(bytes).toLong()
        return java.lang.String.format("%.1f %ciB", value / 1024.0, ci.current())
    }

    private fun hideKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}