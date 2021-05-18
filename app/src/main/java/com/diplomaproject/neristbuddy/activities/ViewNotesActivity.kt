package com.diplomaproject.neristbuddy.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diplomaproject.neristbuddy.R
import com.diplomaproject.neristbuddy.adapter.NotesRecyclerAdapter
import com.diplomaproject.neristbuddy.util.NotesList
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class ViewNotesActivity : AppCompatActivity() {

    var firebaseAuth = FirebaseAuth.getInstance()
    var dbRef = FirebaseDatabase.getInstance().reference

    lateinit var cantFind:TextView
    lateinit var rlProgress: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var recyclerView: RecyclerView
    lateinit var linearLayoutManager: LinearLayoutManager
    var notesList = arrayListOf<NotesList>()
    lateinit var notesRecyclerAdapter: NotesRecyclerAdapter
    lateinit var year: String
    lateinit var txtNoNotes: TextView
    lateinit var fab:FloatingActionButton


    lateinit var branch: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_notes)



        year = intent.getStringExtra("year").toString()
        branch = intent.getStringExtra("branch").toString()
        title = "$year Year ${branch.toUpperCase(Locale.ROOT)}"

        recyclerView = findViewById(R.id.recyclerNotes)
        rlProgress = findViewById(R.id.rlProgress)
        progressBar = findViewById(R.id.progressbar)
        txtNoNotes = findViewById(R.id.txtNoNotes)
        fab=findViewById(R.id.fab)
        cantFind=findViewById(R.id.cantFind)

        rlProgress.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE


        linearLayoutManager = LinearLayoutManager(this)


        dbRef.child("Notes").child(year.toString()).child(branch.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    notesList.clear()
                    for (notesSnap: DataSnapshot in snapshot.children) {
                        try {
                            if (notesSnap.hasChild("image")){
                                val notes = NotesList(
                                        notesSnap.child("name").value as String,
                                        notesSnap.child("notes").value as String,
                                        notesSnap.child("image").value as String,
                                        notesSnap.child("imageName").value as String,
                                        null,
                                        null,
                                        notesSnap.child("uploadedBy").value as String,

                                )
                                notesList.add(notes)
                            }
                            else if (notesSnap.hasChild("pdf")){
                                val notes = NotesList(
                                        notesSnap.child("name").value as String,
                                        notesSnap.child("notes").value as String,
                                        null,
                                        null,
                                        notesSnap.child("pdf").value as String,
                                        notesSnap.child("pdfName").value as String,
                                        notesSnap.child("uploadedBy").value as String,
                                )
                                notesList.add(notes)
                            }
                            else{
                                val notes = NotesList(
                                        notesSnap.child("name").value as String,
                                        notesSnap.child("notes").value as String,
                                        null,
                                        null,
                                        null,
                                        null,
                                        notesSnap.child("uploadedBy").value as String,
                                )
                                notesList.add(notes)
                            }

                            notesRecyclerAdapter =
                                    NotesRecyclerAdapter(this@ViewNotesActivity, notesList,year,branch)
                            recyclerView.layoutManager = linearLayoutManager
                            notesRecyclerAdapter.notifyDataSetChanged()
                            recyclerView.adapter = notesRecyclerAdapter
                        } catch (e: NullPointerException) {
                            println("exception-----> ${e.message}")
                        }
                    }
                    rlProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    if (notesList.size == 0) {
                        txtNoNotes.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        fab.setOnClickListener {
            val intent=Intent(this,UploadNotesActivity::class.java)
            intent.putExtra("year",year)
            intent.putExtra("branch",branch)
            startActivity(intent)
            finish()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu,menu)
        val search= menu?.findItem(R.id.appSearchBar)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Search"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filterFun(newText)
                }
                return true
            }
        })


        return super.onCreateOptionsMenu(menu)
    }
    fun filterFun(strTyped:String){
        val filteredList= arrayListOf<NotesList>()
        for (item in notesList){
            if (item.name.toLowerCase(Locale.ROOT).contains(strTyped.toLowerCase(Locale.ROOT))){
                filteredList.add(item)
            }
        }
        if (filteredList.size==0){
            cantFind.visibility=View.VISIBLE
        }
        else{
            cantFind.visibility=View.GONE
        }
        try {
            notesRecyclerAdapter.filterList(filteredList)
        }
        catch (e:UninitializedPropertyAccessException){
            Log.d("Exception",e.message.toString())
        }

    }

}