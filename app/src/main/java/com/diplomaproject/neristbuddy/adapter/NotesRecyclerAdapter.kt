package com.diplomaproject.neristbuddy.adapter

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.diplomaproject.neristbuddy.R
import com.diplomaproject.neristbuddy.util.NotesList
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

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
                alertDialog.setPositiveButton("Yes",DialogInterface.OnClickListener{dialogInterface, i ->
                    val loadingBar=ProgressDialog(context)
                    loadingBar.setCanceledOnTouchOutside(false)
                    loadingBar.show()
                    FirebaseDatabase.getInstance().reference.child("Notes").child(year).child(branch).child(notesItem.name).removeValue().addOnCompleteListener {
                        if (it.isSuccessful){
                            if (notesItem.image!=null){
                                FirebaseStorage.getInstance().reference.child("images").child(notesItem.imageName!!).delete()
                            }
                            if (notesItem.pdf!=null){
                                FirebaseStorage.getInstance().reference.child("pdf").child(notesItem.pdfName.toString()).delete()
                            }
                            Toast.makeText(context,"Note deleted successfully",Toast.LENGTH_SHORT).show()
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
                val alertDialog=AlertDialog.Builder(context)
                alertDialog.setMessage("Do you want to Download?")
                alertDialog.setPositiveButton("Yes",DialogInterface.OnClickListener{dialogInterface, i ->
                    val url=notesItem.pdf
                    val intent=Intent(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse(url))
                    context.startActivity(intent)
                })
                alertDialog.setNegativeButton("No",DialogInterface.OnClickListener { dialogInterface, i ->

                })
                alertDialog.create()
                alertDialog.show()
            }
        }



    }

    override fun getItemCount(): Int {
        return listOfNotes.size
    }

}


