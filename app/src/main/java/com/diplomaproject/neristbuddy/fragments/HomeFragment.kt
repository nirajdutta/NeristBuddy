package com.diplomaproject.neristbuddy.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.diplomaproject.neristbuddy.R
import com.diplomaproject.neristbuddy.activities.NotesActivity
import com.diplomaproject.neristbuddy.util.PriorityList
import java.util.*
import kotlin.Comparator

class HomeFragment : Fragment() {


    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout

    lateinit var btnNotes: Button
    lateinit var btnDoubts: Button
    lateinit var btnTnp: Button
    lateinit var btnLost: Button
    lateinit var btnEvents: Button
    lateinit var btnCCA: Button



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)


//        recyclerHome = view.findViewById(R.id.recyclerHome)
//        progressLayout = view.findViewById(R.id.progressLayout)
//        progressBar = view.findViewById(R.id.progressBar)
//        progressLayout.visibility = View.GONE


        btnNotes=view.findViewById(R.id.btnNotes)
        btnDoubts=view.findViewById(R.id.btnDoubts)
        btnTnp=view.findViewById(R.id.btnTnP)
        btnLost=view.findViewById(R.id.btnLost)
        btnEvents=view.findViewById(R.id.btnEvents)
        btnCCA=view.findViewById(R.id.btnCCA)


        btnNotes.setOnClickListener {
            startActivity(Intent(activity,NotesActivity::class.java))
        }


        return view
    }

}



