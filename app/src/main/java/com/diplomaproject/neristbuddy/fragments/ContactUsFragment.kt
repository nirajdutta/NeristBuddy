package com.diplomaproject.neristbuddy.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.diplomaproject.neristbuddy.R

class ContactUsFragment : Fragment() {

    lateinit var email1: ImageView
    lateinit var email2: ImageView
    lateinit var dial1: ImageView
    lateinit var dial2: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contact_us, container, false)
        email1 = view.findViewById(R.id.email1)
        email2 = view.findViewById(R.id.email2)
        dial1 = view.findViewById(R.id.dial1)
        dial2 = view.findViewById(R.id.dial2)


        val niraj="nirajdutta@yahoo.com"
        val doley="doleybishal07@gmail.com"

        val ph1="+919101262048"
        val ph2="+918638183170"

        email1.setOnClickListener {
            val intent=Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:$niraj")
            startActivity(intent)
        }
        email2.setOnClickListener {
            val intent=Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:$doley")
            startActivity(intent)
        }
        dial1.setOnClickListener {
            val intent=Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$ph1")
            startActivity(intent)
        }
        dial2.setOnClickListener {
            val intent=Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$ph2")
            startActivity(intent)
        }

        return view
    }

}