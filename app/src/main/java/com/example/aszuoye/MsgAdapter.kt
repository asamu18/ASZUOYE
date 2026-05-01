package com.example.aszuoye

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MsgAdapter(val msgList: List<Msg>) : RecyclerView.Adapter<MsgAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeText: TextView = view.findViewById(R.id.timeText)
        val leftLayout: FrameLayout = view.findViewById(R.id.leftLayout)
        val rightLayout: FrameLayout = view.findViewById(R.id.rightLayout)
        val leftMsg: TextView = view.findViewById(R.id.leftMsg)
        val rightMsg: TextView = view.findViewById(R.id.rightMsg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.msg_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val msg = msgList[position]
        if (msg.time.isNotBlank()) {
            holder.timeText.visibility = View.VISIBLE
            holder.timeText.text = msg.time
        } else {
            holder.timeText.visibility = View.GONE
        }
        if (msg.type == Msg.TYPE_RECEIVED) {
            holder.leftLayout.visibility = View.VISIBLE
            holder.rightLayout.visibility = View.GONE
            holder.leftMsg.text = msg.content
        } else {
            holder.rightLayout.visibility = View.VISIBLE
            holder.leftLayout.visibility = View.GONE
            holder.rightMsg.text = msg.content
        }
    }

    override fun getItemCount() = msgList.size
}
