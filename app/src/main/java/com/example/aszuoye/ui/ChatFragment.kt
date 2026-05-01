package com.example.aszuoye.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aszuoye.Msg
import com.example.aszuoye.MsgAdapter
import com.example.aszuoye.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatFragment : Fragment(R.layout.fragment_chat) {
    private val msgList = ArrayList<Msg>()
    private lateinit var adapter: MsgAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.chatRecyclerView)
        val inputText: EditText = view.findViewById(R.id.chatInputText)
        val sendBtn: Button = view.findViewById(R.id.chatSendBtn)

        initMessages()

        adapter = MsgAdapter(msgList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        scrollToBottom()

        sendBtn.setOnClickListener {
            val content = inputText.text.toString()
            if (content.isNotBlank()) {
                msgList.add(Msg(content, Msg.TYPE_SENT, nowTime()))
                adapter.notifyItemInserted(msgList.size - 1)
                scrollToBottom()
                inputText.setText("")
            }
        }
    }

    fun scrollToBottom() {
        if (this::recyclerView.isInitialized && msgList.isNotEmpty()) {
            recyclerView.scrollToPosition(msgList.size - 1)
        }
    }

    private fun initMessages() {
        if (msgList.isNotEmpty()) return
        msgList.add(Msg("你好啊！", Msg.TYPE_RECEIVED, nowTime()))
        msgList.add(Msg("你是谁？", Msg.TYPE_SENT, nowTime()))
        msgList.add(Msg("我是仿QQ聊天界面示例。", Msg.TYPE_RECEIVED, nowTime()))
    }

    private fun nowTime(): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }
}

