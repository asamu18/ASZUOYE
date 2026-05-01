package com.example.aszuoye

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity2 : AppCompatActivity() {
    private val TAG = "Lifecycle_Main2"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // --- 1. 接收从 MainActivity 传下来的数据 ---
        val receivedMsg = intent.getStringExtra("param1")
        val textView = findViewById<TextView>(R.id.receivedText)
        if (receivedMsg != null) {
            textView.text = receivedMsg
        }

        // --- 2. 向上回传数据并关闭当前界面 ---
        val btnBack = findViewById<Button>(R.id.btn_back_with_data)
        btnBack.setOnClickListener {
            val intent = Intent()
            intent.putExtra("return_param", "我是返回的数据，看到了吗？")
            setResult(RESULT_OK, intent) // 设置返回结果
            finish() // 销毁当前 Activity，回到上一页
        }
    }
    override fun onStart() { super.onStart(); Log.d(TAG, "onStart") }
    override fun onResume() { super.onResume(); Log.d(TAG, "onResume") }
    override fun onPause() { super.onPause(); Log.d(TAG, "onPause") }
    override fun onStop() { super.onStop(); Log.d(TAG, "onStop") }
    override fun onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy") }
    override fun onRestart() { super.onRestart(); Log.d(TAG, "onRestart") }
}