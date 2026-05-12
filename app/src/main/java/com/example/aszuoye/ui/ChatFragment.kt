package com.example.aszuoye.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aszuoye.Msg
import com.example.aszuoye.MsgAdapter
import com.example.aszuoye.R
import com.example.aszuoye.chat.FileUploadAsyncTask
import com.example.aszuoye.chat.FileUploadResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatFragment : Fragment(R.layout.fragment_chat) {
    private val msgList = ArrayList<Msg>()
    private lateinit var adapter: MsgAdapter
    private lateinit var recyclerView: RecyclerView

    private var uploadTask: FileUploadAsyncTask? = null

    private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@registerForActivityResult
        uploadTask?.cancel(true)
        Toast.makeText(requireContext(), R.string.chat_uploading, Toast.LENGTH_SHORT).show()
        val appCtx = requireContext().applicationContext
        uploadTask = FileUploadAsyncTask(appCtx) { result: FileUploadResult ->
            uploadTask = null
            val text = if (result.success) {
                getString(R.string.chat_upload_ok, result.displayName)
            } else {
                getString(R.string.chat_upload_fail, result.displayName, result.detail)
            }
            Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
            if (result.success && result.displayName.isNotBlank()) {
                msgList.add(
                    Msg(
                        content = result.displayName,
                        type = Msg.TYPE_SENT,
                        time = nowTime(),
                        isFile = true
                    )
                )
                adapter.notifyItemInserted(msgList.size - 1)
                scrollToBottom()
            }
        }.also { it.execute(uri) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.chatRecyclerView)
        val inputText: EditText = view.findViewById(R.id.chatInputText)
        val sendBtn: Button = view.findViewById(R.id.chatSendBtn)
        val pickFileBtn: Button = view.findViewById(R.id.chatPickFileBtn)

        initMessages()

        adapter = MsgAdapter(msgList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        scrollToBottom()

        pickFileBtn.setOnClickListener {
            pickFileLauncher.launch("*/*")
        }

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

    override fun onDestroyView() {
        uploadTask?.cancel(true)
        uploadTask = null
        super.onDestroyView()
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
