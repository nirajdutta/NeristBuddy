package com.diplomaproject.neristbuddy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.diplomaproject.neristbuddy.R

class EventsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)
        title="Events"
    }
}