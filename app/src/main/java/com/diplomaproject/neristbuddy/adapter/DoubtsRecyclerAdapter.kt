package com.diplomaproject.neristbuddy.adapter

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.text.Editable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.diplomaproject.neristbuddy.R
import com.diplomaproject.neristbuddy.activities.DiscussionActivity
import com.diplomaproject.neristbuddy.model.Doubt
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DoubtsRecyclerAdapter(val context: Context, private val listOfDoubts: ArrayList<Doubt>): RecyclerView.Adapter<DoubtsRecyclerAdapter.DoubtsViewHolder>() {
    class DoubtsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val doubtCardView:CardView=view.findViewById(R.id.doubtCard)
        val txtAskedBy:TextView=view.findViewById(R.id.txtAskedBy)
        val dateTime:TextView=view.findViewById(R.id.dateTime)
        val txtSubject:TextView=view.findViewById(R.id.txtSubject)
        val txtDoubt:TextView=view.findViewById(R.id.txtDoubt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoubtsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.recycler_doubts_single_row, parent, false)
        return DoubtsViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: DoubtsViewHolder, position: Int) {
        val doubt=listOfDoubts[position]
        holder.txtAskedBy.text=doubt.askedBy
        holder.dateTime.text=doubt.time
        holder.txtSubject.text=doubt.subject
        holder.txtDoubt.text=doubt.doubt

        holder.doubtCardView.setOnClickListener {
            val intent=Intent(context,DiscussionActivity::class.java)
            intent.putExtra("key",doubt.key)
            intent.putExtra("doubt",doubt.doubt)
            context.startActivity(intent)
        }

        holder.doubtCardView.setOnLongClickListener {
            val uid = FirebaseAuth.getInstance().uid.toString()
            if (uid == doubt.uid){
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setTitle("Modify your Doubt")
                alertDialog.setMessage("Do you want to Edit this Doubt?")
                alertDialog.setPositiveButton("Edit", DialogInterface.OnClickListener { dialogInterface, i ->
                    editPost(doubt)
                })

                alertDialog.setNegativeButton("Delete", DialogInterface.OnClickListener { dialogInterface, i ->
                    val loadingBar = ProgressDialog(context)
                    loadingBar.setMessage("Deleting your notes...")
                    loadingBar.setCanceledOnTouchOutside(false)
                    loadingBar.show()
                    FirebaseDatabase.getInstance().reference.child("Doubts").child(doubt.key).removeValue().addOnCompleteListener {
                        if (it.isSuccessful) {
                            FirebaseDatabase.getInstance().reference.child("Discussion").child(doubt.key).removeValue()
                            Toast.makeText(context, "Doubt removed successfully", Toast.LENGTH_SHORT).show()
                            notifyDataSetChanged()
                            loadingBar.dismiss()
                        }
                        loadingBar.dismiss()
                    }
                })
                alertDialog.setNeutralButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                alertDialog.create()
                alertDialog.show()
            }

            return@setOnLongClickListener true
        }
    }

    fun editPost(doubt:Doubt){
        val dbRef=FirebaseDatabase.getInstance().reference
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Ask a new doubt")
        alertDialog.setMessage("Please enter the following details")

        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        val et1 = EditText(context)
        et1.hint = "Please enter the subject name"
        et1.inputType=InputType.TYPE_TEXT_FLAG_CAP_WORDS
        linearLayout.addView(et1)
        val et2 = EditText(context)
        et2.hint = "Please enter your doubt"
        et2.inputType=InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        et2.isSingleLine=false

        et1.setText(doubt.subject)
        et2.setText(doubt.doubt)

        linearLayout.addView(et2)
        alertDialog.setView(linearLayout)

        alertDialog.setPositiveButton("Post",
                DialogInterface.OnClickListener { dialogInterface, i ->

                    if (et1.text.isNotEmpty()&&et2.text.isNotEmpty()){
                        val loadingBar = ProgressDialog(context)
                        loadingBar.setMessage("Updating...")
                        loadingBar.setCanceledOnTouchOutside(false)
                        loadingBar.show()
                        val sdf= SimpleDateFormat("dd/MM/yyyy hh:mm aa")
                        val time=sdf.format(Date())
                        println(time)

                        val doubtkey=doubt.key
                        val post=Doubt(
                                doubt.askedBy,
                                "(edited) $time",
                                et1.text.toString(),
                                et2.text.toString(),
                                doubt.uid,
                                doubtkey.toString()
                        )
                        dbRef.child("Doubts").child(doubtkey).setValue(post).addOnCompleteListener {
                            loadingBar.dismiss()
                            notifyDataSetChanged()
                            Toast.makeText(context, "Doubt Successfully edited", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        if (et1.text.isEmpty()){
                            et1.error="Do not leave this field empty"
                        }
                        if (et2.text.isEmpty()){
                            et2.error="Do not leave this field empty"
                        }
                    }

                })
        alertDialog.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialogInterface, i -> })
        alertDialog.show()
    }

    override fun getItemCount(): Int {
        return listOfDoubts.size
    }
}