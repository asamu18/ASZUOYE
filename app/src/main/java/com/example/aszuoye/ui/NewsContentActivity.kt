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
            val downloadUrl = intent.getStringExtra(EXTRA_DOWNLOAD_URL).orEmpty()
            val downloadName = intent.getStringExtra(EXTRA_DOWNLOAD_FILENAME).orEmpty()
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.newsContentContainer,
                    NewsContentFragment.newInstance(title, content, downloadUrl, downloadName)
                )
                .commit()
        }
    }

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_CONTENT = "extra_content"
        const val EXTRA_DOWNLOAD_URL = "extra_download_url"
        const val EXTRA_DOWNLOAD_FILENAME = "extra_download_filename"
    }
}

