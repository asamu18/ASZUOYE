package com.example.aszuoye

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aszuoye.data.UserDb
import com.example.aszuoye.ui.UserListActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameEdit: EditText = findViewById(R.id.usernameEdit)
        val passwordEdit: EditText = findViewById(R.id.passwordEdit)
        val rememberCheck: CheckBox = findViewById(R.id.rememberCheck)
        val loginBtn: Button = findViewById(R.id.loginBtn)
        val userListBtn: Button = findViewById(R.id.userListBtn)

        val sp = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val remembered = sp.getBoolean(KEY_REMEMBER, false)
        if (remembered) {
            rememberCheck.isChecked = true
            usernameEdit.setText(sp.getString(KEY_USERNAME, "").orEmpty())
            passwordEdit.setText(sp.getString(KEY_PASSWORD, "").orEmpty())
        }

        userListBtn.setOnClickListener {
            startActivity(Intent(this, UserListActivity::class.java))
        }

        loginBtn.setOnClickListener {
            val username = usernameEdit.text.toString().trim()
            val password = passwordEdit.text.toString()

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (rememberCheck.isChecked) {
                sp.edit()
                    .putBoolean(KEY_REMEMBER, true)
                    .putString(KEY_USERNAME, username)
                    .putString(KEY_PASSWORD, password)
                    .apply()
            } else {
                sp.edit()
                    .putBoolean(KEY_REMEMBER, false)
                    .remove(KEY_USERNAME)
                    .remove(KEY_PASSWORD)
                    .apply()
            }

            UserDb(this).upsertLoginUser(username)

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    companion object {
        private const val PREFS_NAME = "login_prefs"
        private const val KEY_REMEMBER = "remember"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
    }
}

