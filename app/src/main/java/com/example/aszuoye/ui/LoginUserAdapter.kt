package com.example.aszuoye.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aszuoye.R
import com.example.aszuoye.data.LoginUser
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LoginUserAdapter(private val users: List<LoginUser>) :
    RecyclerView.Adapter<LoginUserAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val usernameText: TextView = view.findViewById(R.id.usernameText)
        val lastLoginText: TextView = view.findViewById(R.id.lastLoginText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_login_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.usernameText.text = user.username
        holder.lastLoginText.text = "最近登录：${formatTime(user.lastLoginAt)}"
    }

    override fun getItemCount(): Int = users.size

    private fun formatTime(ts: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date(ts))
    }
}

