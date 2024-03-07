package com.example.snapz.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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

class MessageAdapter(messages: ArrayList<MessageModel>, val longListener: OnLongCLickListener) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    val messages = messages



    class ViewHolder(viewItem: View) : RecyclerView.ViewHolder(viewItem){
        val sent: ConstraintLayout = viewItem.findViewById(R.id.sentLay)
        val messageSent: TextView = viewItem.findViewById(R.id.tvSent)
        val imageSentLay: ConstraintLayout = viewItem.findViewById(R.id.imageSent)
        val imageSent: ImageView = viewItem.findViewById(R.id.ivSent)

        val recived: ConstraintLayout = viewItem.findViewById(R.id.recivedLay)
        val messageRecived: TextView = viewItem.findViewById(R.id.tvRecived)
        val imageRecivedLay: ConstraintLayout = viewItem.findViewById(R.id.imageRecived)
        val imageRecived: ImageView = viewItem.findViewById(R.id.ivRecived)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val sender: Boolean = messages[position].sender == FireHelper.user!!.uid
        val type: String = messages[position].type
        val message: String = messages[position].text
        val link: String = messages[position].link

        val imageLong = longListener.onLognClickImageListener(position, holder.itemView)
        val messageLong = longListener.onLongClickMessageLIstener(position, holder.itemView)

        holder.messageSent.setOnLongClickListener {
            messageLong
            true
        }
        holder.imageSentLay.setOnLongClickListener {
            imageLong
            true
        }



        if(sender){
            holder.recived.visibility = View.GONE

            if(type == "Text"){
                holder.imageSentLay.visibility = View.GONE

                holder.messageSent.visibility = View.VISIBLE 
                holder.messageSent.text = message
            }
            else{
                holder.messageSent.visibility = View.GONE

                Glide.with(holder.itemView.context).load(link).fitCenter().into(holder.imageSent)
            }
        }
        else{
            holder.sent.visibility = View.GONE

            if(type == "Text"){
                holder.imageRecivedLay.visibility = View.GONE

                holder.messageRecived.text = message

            }
            else{
                holder.messageRecived.visibility = View.GONE

                Glide.with(holder.itemView.context).load(link).fitCenter().into(holder.imageRecived)
            }
        }
    }
}