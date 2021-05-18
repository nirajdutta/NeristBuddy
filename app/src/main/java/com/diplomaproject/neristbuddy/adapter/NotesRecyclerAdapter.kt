package com.diplomaproject.neristbuddy.adapter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.diplomaproject.neristbuddy.R
import com.diplomaproject.neristbuddy.util.NotesList
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.File
import java.text.CharacterIterator
import java.text.StringCharacterIterator

class NotesRecyclerAdapter(var context: Context, var listOfNotes: ArrayList<NotesList>,val year:String,val branch:String) : RecyclerView.Adapter<NotesRecyclerAdapter.NotesViewHolder>() {
    class NotesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rlNotesItem:CardView=view.findViewById(R.id.notesItem)
        val txtName: TextView = view.findViewById(R.id.txtName)
        val txtNotes: TextView = view.findViewById(R.id.txtNotes)
        val imgNotes: ImageView = view.findViewById(R.id.imgNotes)
        val txtUploadedBy:TextView=view.findViewById(R.id.uploadedBy)
        val cardView:CardView=view.findViewById(R.id.pdfView)
        val txtPdfName:TextView=view.findViewById(R.id.pdfName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.recycler_notes_single_row, parent, false)
        return NotesViewHolder(layoutInflater)
    }

    fun filterList(filteredList: ArrayList<NotesList>){
        listOfNotes=filteredList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        
        var notesItem=listOfNotes[position]
        
        holder.rlNotesItem.setOnLongClickListener{
            val sharedPreferences=context.getSharedPreferences(R.string.saved_preferences.toString(),MODE_PRIVATE)
            val username=sharedPreferences.getString("userName","username")
//            println(username)
            if (username==notesItem.uploadedBy){
//                Toast.makeText(context,"true",Toast.LENGTH_SHORT).show()
                val alertDialog=AlertDialog.Builder(context)
                alertDialog.setMessage("Do you want to Delete this Note?")
                alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                    val loadingBar = ProgressDialog(context)
                    loadingBar.setMessage("Deleting your notes...")
                    loadingBar.setCanceledOnTouchOutside(false)
                    loadingBar.show()
                    FirebaseDatabase.getInstance().reference.child("Notes").child(year).child(branch).child(notesItem.name).removeValue().addOnCompleteListener {
                        if (it.isSuccessful) {
                            if (notesItem.image != null) {
                                FirebaseStorage.getInstance().reference.child("images").child(notesItem.imageName!!).delete()
                            }
                            if (notesItem.pdf != null) {
                                FirebaseStorage.getInstance().reference.child("pdf").child(notesItem.pdfName.toString()).delete()
                            }
                            Toast.makeText(context, "Note deleted successfully", Toast.LENGTH_SHORT).show()
                            notifyDataSetChanged()
                            loadingBar.dismiss()
                        }
                        loadingBar.dismiss()
                    }
                })
                alertDialog.setNegativeButton("No",DialogInterface.OnClickListener { dialogInterface, i ->

                })
                alertDialog.create()
                alertDialog.show()


            }

            return@setOnLongClickListener true
        }
        holder.txtName.text=notesItem.name.toString()
        holder.txtNotes.text=notesItem.notes.toString()
        holder.txtUploadedBy.text=notesItem.uploadedBy.toString()
        if (notesItem.image!=null)
        {
            holder.imgNotes.visibility=View.VISIBLE
            Picasso.get().load(notesItem.image).placeholder(R.drawable.placeholder).error(R.drawable.error_image).into(holder.imgNotes)
        }
        if (notesItem.pdf!=null){
            holder.cardView.visibility=View.VISIBLE
            holder.txtPdfName.text=notesItem.pdfName
            holder.cardView.setOnClickListener {
                val pdfFile=File(Environment.getExternalStorageDirectory().canonicalPath+"/NeristBuddy/${notesItem.pdfName}")
                if (pdfFile.exists()){
                    val intent = Intent(Intent.ACTION_VIEW)
                    val pdfUri= FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider",pdfFile)
                    intent.setDataAndType(Uri.parse(pdfUri.toString()),"application/pdf")
                    intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    context.startActivity(intent)
                }
                else{
                    val alertDialog=AlertDialog.Builder(context)
                    alertDialog.setMessage("Do you want to Download?")
                    alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->

                        if (ContextCompat.checkSelfPermission(context,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(context as Activity,
                                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                    11)


                        }

                        val loadingBar = ProgressDialog(context)
                        loadingBar.setMessage("Downloading..")
                        loadingBar.setCanceledOnTouchOutside(false)
                        loadingBar.show()
                        val firebaseStorage = FirebaseStorage.getInstance().getReferenceFromUrl(notesItem.pdf.toString())

                        val rootPath = File(Environment.getExternalStorageDirectory(), "NeristBuddy")
                        if (!rootPath.exists()) {
                            rootPath.mkdirs()
                        }
                        val file = File(rootPath, notesItem.pdfName.toString())
                        val down = firebaseStorage.getFile(file)
                        down.addOnProgressListener {
                            loadingBar.setMessage("Downloading ${notesItem.pdfName.toString()}\n " +
                                    "${humanReadableByteCountBin(it.bytesTransferred)}/${humanReadableByteCountBin(it.totalByteCount)}")
                        }

                        down.addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(context, "${notesItem.pdfName} downloaded successfully", Toast.LENGTH_SHORT).show()
                                loadingBar.dismiss()
                                val intent = Intent(Intent.ACTION_VIEW)
                                val pdfUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file)
                                intent.setDataAndType(Uri.parse(pdfUri.toString()), "application/pdf")
                                intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                context.startActivity(intent)
                            }
                        }


                    })
                    alertDialog.setNegativeButton("No",DialogInterface.OnClickListener { dialogInterface, i ->

                    })
                    alertDialog.create()
                    alertDialog.show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listOfNotes.size
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
        return java.lang.String.format("%.1f %cB", value / 1024.0, ci.current())
    }


}
