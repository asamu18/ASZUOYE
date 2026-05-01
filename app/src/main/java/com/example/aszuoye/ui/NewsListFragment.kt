package com.example.aszuoye.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aszuoye.R

class NewsListFragment : Fragment(R.layout.fragment_news_list) {
    private val newsList: List<News> = NewsRepository.sampleNews()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.newsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = NewsAdapter(newsList) { news ->
            val intent = Intent(requireContext(), NewsContentActivity::class.java)
            intent.putExtra(NewsContentActivity.EXTRA_TITLE, news.title)
            intent.putExtra(NewsContentActivity.EXTRA_CONTENT, news.content)
            startActivity(intent)
        }
    }
}

