package com.diplomaproject.neristbuddy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diplomaproject.neristbuddy.R
import com.diplomaproject.neristbuddy.util.NotesList
import com.squareup.picasso.Picasso

class NotesRecyclerAdapter(var context: Context, var listOfNotes: ArrayList<NotesList>) : RecyclerView.Adapter<NotesRecyclerAdapter.NotesViewHolder>() {
    class NotesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txtName)
        val txtNotes: TextView = view.findViewById(R.id.txtNotes)
        val imgNotes: ImageView = view.findViewById(R.id.imgNotes)
        val txtUploadedBy:TextView=view.findViewById(R.id.uploadedBy)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.recycler_notes_single_row, parent, false)
        return NotesViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        var notesItem=listOfNotes[position]
        holder.txtName.text=notesItem.topicName.toString()
        holder.txtNotes.text=notesItem.notes.toString()
        holder.txtUploadedBy.text=notesItem.uploadedBy.toString()
        Picasso.get().load(notesItem.image).placeholder(R.drawable.placeholder).error(R.drawable.error_image).into(holder.imgNotes)

    }

    override fun getItemCount(): Int {
        return listOfNotes.size
    }

}