package com.diplomaproject.neristbuddy.adapter

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.diplomaproject.neristbuddy.R
import com.diplomaproject.neristbuddy.model.Doubt
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

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
        holder.doubtCardView.setOnLongClickListener {
            val uid = FirebaseAuth.getInstance().uid.toString()
            if (uid == doubt.uid){
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setMessage("Do you want to Delete this Doubt?")
                alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                    val loadingBar = ProgressDialog(context)
                    loadingBar.setMessage("Deleting your notes...")
                    loadingBar.setCanceledOnTouchOutside(false)
                    loadingBar.show()
                    FirebaseDatabase.getInstance().reference.child("Doubts").child(doubt.key).removeValue().addOnCompleteListener {
                        if (it.isSuccessful) {

                            Toast.makeText(context, "Note deleted successfully", Toast.LENGTH_SHORT).show()
                            notifyDataSetChanged()
                            loadingBar.dismiss()
                        }
                        loadingBar.dismiss()
                    }
                })
                alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                alertDialog.create()
                alertDialog.show()
            }

            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return listOfDoubts.size
    }
}