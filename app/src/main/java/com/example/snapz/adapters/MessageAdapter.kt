package com.example.snapz.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapz.Classes.FireHelper
import com.example.snapz.Classes.MessageModel
import com.example.snapz.R

interface OnLongCLickListener{
    fun onLongClickMessageLIstener(position: Int, view: View)
    fun onLognClickImageListener(position: Int, view: View)
}

class MessageAdapter(messages: ArrayList<MessageModel>, val onLongListener: OnLongCLickListener) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    val messages = messages

    class ViewHolder(viewItem: View, onLongListener: OnLongCLickListener) : RecyclerView.ViewHolder(viewItem) {
        val sent: ConstraintLayout = viewItem.findViewById(R.id.sentLay)
        val messageSent: TextView = viewItem.findViewById(R.id.tvSent)
        val imageSentLay: ConstraintLayout = viewItem.findViewById(R.id.imageSent)
        val imageSent: ImageView = viewItem.findViewById(R.id.ivSent)

        val received: ConstraintLayout = viewItem.findViewById(R.id.recivedLay)
        val messageReceived: TextView = viewItem.findViewById(R.id.tvRecived)
        val imageReceivedLay: ConstraintLayout = viewItem.findViewById(R.id.imageRecived)
        val imageReceived: ImageView = viewItem.findViewById(R.id.ivRecived)

        init {
            messageSent.setOnLongClickListener {
                onLongListener.onLongClickMessageLIstener(adapterPosition, itemView)
                return@setOnLongClickListener true
            }
            imageSentLay.setOnLongClickListener {

                onLongListener.onLognClickImageListener(adapterPosition, itemView)
                return@setOnLongClickListener true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return ViewHolder(view, onLongListener)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val sender: Boolean = messages[position].sender == FireHelper.user!!.uid
        val type: String = messages[position].type
        val message: String = messages[position].text
        val link: String = messages[position].link

        if (sender) {
            holder.received.visibility = View.GONE

            if (type == "Text") {
                holder.imageSentLay.visibility = View.GONE

                holder.messageSent.visibility = View.VISIBLE
                holder.messageSent.text = message
            } else {
                holder.messageSent.visibility = View.GONE

                Glide.with(holder.itemView.context).load(link).fitCenter().into(holder.imageSent)
            }
        } else {
            holder.sent.visibility = View.GONE

            if (type == "Text") {
                holder.imageReceivedLay.visibility = View.GONE

                holder.messageReceived.text = message

            } else {
                holder.messageReceived.visibility = View.GONE

                Glide.with(holder.itemView.context).load(link).fitCenter()
                    .into(holder.imageReceived)
            }
        }
    }

}