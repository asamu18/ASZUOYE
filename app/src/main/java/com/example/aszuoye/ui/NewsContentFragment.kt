package com.example.aszuoye.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.example.aszuoye.R
import com.example.aszuoye.download.FileDownloadForegroundService

class NewsContentFragment : Fragment(R.layout.fragment_news_content) {

    private var downloadUrl: String = ""
    private var downloadFileName: String = ""

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startDownloadService()
        } else {
            Toast.makeText(requireContext(), R.string.download_need_notification, Toast.LENGTH_LONG).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = requireArguments()
        val titleView: TextView = view.findViewById(R.id.newsContentTitle)
        val bodyView: TextView = view.findViewById(R.id.newsContentBody)
        titleView.text = args.getString(ARG_TITLE).orEmpty()
        bodyView.text = args.getString(ARG_CONTENT).orEmpty()
        downloadUrl = args.getString(ARG_DOWNLOAD_URL).orEmpty()
        downloadFileName = args.getString(ARG_DOWNLOAD_FILENAME).orEmpty()

        view.findViewById<MaterialButton>(R.id.newsDownloadBtn).setOnClickListener {
            if (downloadUrl.isBlank()) {
                Toast.makeText(requireContext(), R.string.download_no_url, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val ok = ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (ok) {
                    startDownloadService()
                } else {
                    requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                startDownloadService()
            }
        }
    }

    private fun startDownloadService() {
        FileDownloadForegroundService.start(requireContext(), downloadUrl, downloadFileName)
        Toast.makeText(requireContext(), R.string.download_started_toast, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_CONTENT = "content"
        private const val ARG_DOWNLOAD_URL = "download_url"
        private const val ARG_DOWNLOAD_FILENAME = "download_filename"

        fun newInstance(
            title: String,
            content: String,
            downloadUrl: String,
            downloadFileName: String
        ): NewsContentFragment {
            val fragment = NewsContentFragment()
            fragment.arguments = bundleOf(
                ARG_TITLE to title,
                ARG_CONTENT to content,
                ARG_DOWNLOAD_URL to downloadUrl,
                ARG_DOWNLOAD_FILENAME to downloadFileName
            )
            return fragment
        }
    }
}
