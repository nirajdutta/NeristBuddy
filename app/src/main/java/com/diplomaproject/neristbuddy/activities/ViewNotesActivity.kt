package com.diplomaproject.neristbuddy.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diplomaproject.neristbuddy.R
import com.diplomaproject.neristbuddy.adapter.NotesRecyclerAdapter
import com.diplomaproject.neristbuddy.util.NotesList

class ViewNotesActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var linearLayoutManager: LinearLayoutManager
    var notesList = arrayListOf<NotesList>(NotesList("Niraj", "This is new notes", "abcd"),
            NotesList("Doley", "This is my notes", "xyz"),NotesList("Niraj", "This is new notes", "abcd"),
    NotesList("Doley", "This is my notes", "xyz"),NotesList("Niraj", "This is new notes", "abcd"),
            NotesList("Doley", "This is my notes", "xyz"),NotesList("Niraj", "This is new notes", "abcd"),
            NotesList("Doley", "This is my notes", "xyz"),NotesList("Niraj", "This is new notes", "abcd"),
            NotesList("Doley", "This is my notes", "xyz"),NotesList("Niraj", "This is new notes", "abcd"),
            NotesList("Doley", "This is my notes", "xyz"))
    lateinit var notesRecyclerAdapter: NotesRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_notes)
        val year = intent.getStringExtra("year")
        val branch = intent.getStringExtra("branch")
        title = "$year-$branch"

        recyclerView = findViewById(R.id.recyclerNotes)
        linearLayoutManager = LinearLayoutManager(this)
        notesRecyclerAdapter = NotesRecyclerAdapter(this, notesList)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = notesRecyclerAdapter
    }
}