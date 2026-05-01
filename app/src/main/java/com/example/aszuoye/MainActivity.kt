package com.example.aszuoye

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
// 确保下面这一行对应你的包名
import com.example.aszuoye.Msg



class MainActivity : AppCompatActivity() {
    private val msgList = ArrayList<Msg>()
    private lateinit var adapter: MsgAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. 设置自定义 Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 2. 设置侧滑菜单按钮 (左上角三横线)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size)
        }

        // 3. 初始化 RecyclerView
        msgList.add(Msg("你好啊！", Msg.TYPE_RECEIVED))
        msgList.add(Msg("你是谁？", Msg.TYPE_SENT))

        val recyclerView: androidx.recyclerview.widget.RecyclerView = findViewById(R.id.recyclerView)
        adapter = MsgAdapter(msgList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 4. 发送逻辑
        val sendBtn: Button = findViewById(R.id.sendBtn)
        val inputText: EditText = findViewById(R.id.inputText)
        sendBtn.setOnClickListener {
            val content = inputText.text.toString()
            if (content.isNotEmpty()) {
                msgList.add(Msg(content, Msg.TYPE_SENT))
                adapter.notifyItemInserted(msgList.size - 1)
                recyclerView.scrollToPosition(msgList.size - 1)
                inputText.setText("")
            }
        }
    }

    // 关键：点击左上角图标打开侧滑菜单
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        when (item.itemId) {
            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }
}
