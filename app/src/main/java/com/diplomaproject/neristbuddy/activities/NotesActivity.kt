package com.diplomaproject.neristbuddy.activities

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.diplomaproject.neristbuddy.R

class NotesActivity : AppCompatActivity() {
    lateinit var btn1st: Button
    lateinit var btn2nd: Button
    lateinit var btn3rd: Button
    lateinit var btn4th: Button
    lateinit var btn5th: Button
    lateinit var btn6th: Button
    lateinit var llBranch: LinearLayout
    lateinit var rlNotes: RelativeLayout
    lateinit var rlYears: RelativeLayout
    lateinit var yearName: TextView
    lateinit var imgCancel: ImageView
    lateinit var choose: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        title="Notes"

        llBranch = findViewById(R.id.llBranch)
        llBranch.visibility = View.GONE

        rlNotes = findViewById(R.id.rlNotes)
        rlYears = findViewById(R.id.rlYears)
        choose=findViewById(R.id.choose)

        btn1st = findViewById(R.id.btn1stYr)
        btn2nd = findViewById(R.id.btn2nd)
        btn3rd = findViewById(R.id.btn3rd)
        btn4th = findViewById(R.id.btn4th)
        btn5th = findViewById(R.id.btn5th)
        btn6th = findViewById(R.id.btn6th)

        imgCancel = findViewById(R.id.imgCancel)
        yearName = findViewById(R.id.yearName)




        btn1st.setOnClickListener {

            choose.text="Choose your Department"
            rlYears.isEnabled = false
            rlYears.visibility = View.GONE
            llBranch.visibility = View.VISIBLE
            yearName.text = "1st Year"
            imgCancel.setOnClickListener {
                llBranch.visibility = View.GONE
                llBranch.isEnabled = false
                rlYears.visibility = View.VISIBLE
                choose.text="Choose your year of study"
            }

        }
        btn2nd.setOnClickListener {

            choose.text="Choose your Department"
            rlYears.isEnabled = false
            rlYears.visibility = View.GONE
            llBranch.visibility = View.VISIBLE
            yearName.text = "2nd Year"
            imgCancel.setOnClickListener {
                llBranch.visibility = View.GONE
                llBranch.isEnabled = false
                rlYears.visibility = View.VISIBLE
                choose.text="Choose your year of study"
            }

        }
        btn3rd.setOnClickListener {

            choose.text="Choose your Department"
            rlYears.isEnabled = false
            rlYears.visibility = View.GONE
            llBranch.visibility = View.VISIBLE
            yearName.text = "3rd Year"
            imgCancel.setOnClickListener {
                llBranch.visibility = View.GONE
                llBranch.isEnabled = false
                rlYears.visibility = View.VISIBLE
                choose.text="Choose your year of study"
            }

        }
        btn4th.setOnClickListener {

            choose.text="Choose your Department"
            rlYears.isEnabled = false
            rlYears.visibility = View.GONE
            llBranch.visibility = View.VISIBLE
            yearName.text = "4th Year"
            imgCancel.setOnClickListener {
                llBranch.visibility = View.GONE
                llBranch.isEnabled = false
                rlYears.visibility = View.VISIBLE
                choose.text="Choose your year of study"
            }

        }
        btn5th.setOnClickListener {

            choose.text="Choose your Department"
            rlYears.isEnabled = false
            rlYears.visibility = View.GONE
            llBranch.visibility = View.VISIBLE
            yearName.text = "5th Year"
            imgCancel.setOnClickListener {
                choose.text="Choose your year of study"
                llBranch.visibility = View.GONE
                llBranch.isEnabled = false
                rlYears.visibility = View.VISIBLE
            }

        }

        btn6th.setOnClickListener {

            choose.text="Choose your Department"
            rlYears.isEnabled = false
            rlYears.visibility = View.GONE

            llBranch.visibility = View.VISIBLE
            yearName.text = "6th Year"
            imgCancel.setOnClickListener {
                choose.text="Choose your year of study"
                llBranch.visibility = View.GONE
                llBranch.isEnabled = false
                rlYears.visibility = View.VISIBLE
            }

        }
    }

    override fun onBackPressed() {
        if (choose.text.toString()=="Choose your Department"){
            choose.text="Choose your year of study"
            llBranch.visibility = View.GONE
            llBranch.isEnabled = false
            rlYears.visibility = View.VISIBLE
        }
        else{
            super.onBackPressed()
        }

    }
}