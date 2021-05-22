package com.diplomaproject.neristbuddy.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diplomaproject.neristbuddy.R
import com.diplomaproject.neristbuddy.model.MessageModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class MessageRecyclerAdapter(val context: Context, val listOfMessages: ArrayList<MessageModel>, val doubtKey: String) : RecyclerView.Adapter<MessageRecyclerAdapter.MessageViewHolder>() {
    val uid = FirebaseAuth.getInstance().uid.toString()
    val dbRef = FirebaseDatabase.getInstance().reference

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val senderLayout: LinearLayout = view.findViewById(R.id.message_sender_layout)
        val senderName: TextView = view.findViewById(R.id.message_sender_name)
        val senderMsg: TextView = view.findViewById(R.id.message_sender_msg)
        val senderTime: TextView = view.findViewById(R.id.message_sender_time)
        val senderImage: ImageView = view.findViewById(R.id.senderImage)
        val receiverLayout: LinearLayout = view.findViewById(R.id.message_receiver_layout)
        val receiverName: TextView = view.findViewById(R.id.message_receiver_name)
        val receiverMsg: TextView = view.findViewById(R.id.message_receiver_msg)
        val receiverTime: TextView = view.findViewById(R.id.message_receiver_time)
        val receiverImage: ImageView = view.findViewById(R.id.receiverImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.recycler_message_single_row, parent, false)
        return MessageViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = listOfMessages[position]
        if (uid == message.uid) {
            holder.senderLayout.visibility = View.VISIBLE
            holder.senderName.text = message.name
            holder.senderTime.text = message.time
            if (message.image == null) {

                holder.senderMsg.text = message.message

            } else {
                holder.senderMsg.visibility = View.GONE
                holder.senderImage.visibility = View.VISIBLE
                Picasso.get().load(message.image).placeholder(R.drawable.placeholder).into(holder.senderImage)
            }
            holder.senderLayout.setOnLongClickListener {
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setTitle("Delete message")
                alertDialog.setMessage("Do you want to Delete this message?")
                alertDialog.setPositiveButton("Delete", DialogInterface.OnClickListener { dialogInterface, i ->
                    dbRef.child("Discussion").child(doubtKey).child(message.msgKey).removeValue().addOnCompleteListener {
                        if (message.image!=null){
                            FirebaseStorage.getInstance().getReferenceFromUrl(message.image).delete()
                        }
                    }

                })
                alertDialog.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                alertDialog.create()
                alertDialog.show()

                return@setOnLongClickListener true
            }
        } else {
            holder.receiverLayout.visibility = View.VISIBLE
            holder.receiverName.text = message.name
            holder.receiverTime.text = message.time
            if (message.image == null) {
                holder.receiverMsg.text = message.message
            } else {
//                holder.receiverLayout.visibility = View.VISIBLE
//                holder.receiverName.text = message.name
//                holder.receiverTime.text = message.time
                holder.receiverMsg.visibility = View.GONE
                holder.receiverImage.visibility = View.VISIBLE
                Picasso.get().load(message.image).placeholder(R.drawable.placeholder).into(holder.receiverImage)
            }
        }


    }

    override fun getItemCount(): Int {
        return listOfMessages.size
    }
}