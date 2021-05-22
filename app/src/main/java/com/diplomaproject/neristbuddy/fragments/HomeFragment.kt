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
import com.diplomaproject.neristbuddy.activities.*

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



        btnNotes=view.findViewById(R.id.btnNotes)
        btnDoubts=view.findViewById(R.id.btnDoubts)
        btnTnp=view.findViewById(R.id.btnTnP)
//        btnLost=view.findViewById(R.id.btnLost)
        btnEvents=view.findViewById(R.id.btnEvents)
        btnCCA=view.findViewById(R.id.btnCCA)

        btnNotes.setOnClickListener {
            startActivity(Intent(activity,NotesActivity::class.java))
        }
        btnDoubts.setOnClickListener {
            startActivity(Intent(activity,DoubtsActivity::class.java))
        }
        btnEvents.setOnClickListener {
            startActivity(Intent(activity, EventsActivity::class.java))
        }
        btnTnp.setOnClickListener {
            startActivity(Intent(activity, tnpactivity::class.java))
        }
        btnCCA.setOnClickListener {
            startActivity(Intent(activity, cca_activity::class.java))
        }

        return view
    }

}



