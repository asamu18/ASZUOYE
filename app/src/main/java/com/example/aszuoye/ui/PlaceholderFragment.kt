package com.example.aszuoye.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.aszuoye.R

class PlaceholderFragment : Fragment(R.layout.fragment_placeholder) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textView: TextView = view.findViewById(R.id.placeholderText)
        textView.text = requireArguments().getString(ARG_TEXT).orEmpty()
    }

    companion object {
        private const val ARG_TEXT = "text"

        fun newInstance(text: String): PlaceholderFragment {
            val fragment = PlaceholderFragment()
            fragment.arguments = bundleOf(ARG_TEXT to text)
            return fragment
        }
    }
}

