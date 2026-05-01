package com.example.aszuoye.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.aszuoye.R

class NewsContentFragment : Fragment(R.layout.fragment_news_content) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val titleView: TextView = view.findViewById(R.id.newsContentTitle)
        val bodyView: TextView = view.findViewById(R.id.newsContentBody)
        titleView.text = requireArguments().getString(ARG_TITLE).orEmpty()
        bodyView.text = requireArguments().getString(ARG_CONTENT).orEmpty()
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_CONTENT = "content"

        fun newInstance(title: String, content: String): NewsContentFragment {
            val fragment = NewsContentFragment()
            fragment.arguments = bundleOf(
                ARG_TITLE to title,
                ARG_CONTENT to content
            )
            return fragment
        }
    }
}

