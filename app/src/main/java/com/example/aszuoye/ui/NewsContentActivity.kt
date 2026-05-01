package com.example.aszuoye.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aszuoye.R

class NewsContentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_content)

        if (savedInstanceState == null) {
            val title = intent.getStringExtra(EXTRA_TITLE).orEmpty()
            val content = intent.getStringExtra(EXTRA_CONTENT).orEmpty()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.newsContentContainer, NewsContentFragment.newInstance(title, content))
                .commit()
        }
    }

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_CONTENT = "extra_content"
    }
}

