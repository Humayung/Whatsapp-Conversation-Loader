package com.example.waconversationloader

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.paris.extensions.style
import kotlinx.android.synthetic.main.item_bubble.view.*
import java.util.ArrayList

class ChatListAdapter(private var list: ArrayList<ChatItem>) : RecyclerView.Adapter<ChatListAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        Log.d("hey", list.toString())
        return Holder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_bubble,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    fun getData(): ArrayList<ChatItem> {
        return list
    }

    fun replace(list: ArrayList<ChatItem>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = list[position]
        val iv = holder.itemView
        iv.text.text = item.text

        iv.date.text = item.date
        val isSender = item.isSender
        val showDate = item.showDate
        if (showDate) iv.date_layout.visibility = View.VISIBLE
        else iv.date_layout.visibility = View.GONE

        if (isSender) {
            iv.time.text = item.time
            iv.text.text = item.text
            iv.sender.visibility = View.VISIBLE
            iv.receiver.visibility = View.GONE
        } else {
            iv.time2.text = item.time
            iv.text2.text = item.text
            iv.sender.visibility = View.GONE
            iv.receiver.visibility = View.VISIBLE
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){}
}