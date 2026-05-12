package com.example.aszuoye.download

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.example.aszuoye.MainActivity
import com.example.aszuoye.R
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/**
 * 前台服务：在后台下载文件并显示常驻通知（点击下载后触发）。
 */
class FileDownloadForegroundService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra(EXTRA_DOWNLOAD_URL)?.trim().orEmpty()
        val rawName = intent?.getStringExtra(EXTRA_DOWNLOAD_FILENAME)?.trim().orEmpty()
        if (url.isEmpty()) {
            stopSelf()
            return START_NOT_STICKY
        }
        val safeName = sanitizeFileName(rawName.ifEmpty { url.substringAfterLast('/') }.ifEmpty { "download.dat" })

        ensureChannel()
        val initial = buildNotification(getString(R.string.download_notify_progress, safeName), true)
        startForeground(NOTIFICATION_ID, initial)

        thread(name = "file-download") {
            var conn: HttpURLConnection? = null
            try {
                conn = (URL(url).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 25_000
                    readTimeout = 120_000
                    instanceFollowRedirects = true
                }
                conn.connect()
                if (conn.responseCode !in 200..299) {
                    throw IllegalStateException("HTTP ${conn.responseCode}")
                }
                val dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    ?: throw IllegalStateException("no download dir")
                if (!dir.exists()) dir.mkdirs()
                val outFile = File(dir, safeName)
                conn.inputStream.use { input ->
                    FileOutputStream(outFile).use { output ->
                        input.copyTo(output)
                    }
                }
                val done = buildNotification(
                    getString(R.string.download_notify_done, safeName),
                    false
                )
                val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.notify(NOTIFICATION_ID, done)
            } catch (e: Exception) {
                val fail = buildNotification(
                    getString(R.string.download_notify_fail, e.message ?: "error"),
                    false
                )
                val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.notify(NOTIFICATION_ID, fail)
            } finally {
                conn?.disconnect()
                ServiceCompat.stopForeground(this@FileDownloadForegroundService, ServiceCompat.STOP_FOREGROUND_DETACH)
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val nm = getSystemService(NotificationManager::class.java)
        val ch = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.download_channel_name),
            NotificationManager.IMPORTANCE_LOW
        )
        nm.createNotificationChannel(ch)
    }

    private fun buildNotification(text: String, ongoing: Boolean): Notification {
        val launch = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(getString(R.string.download_notify_title))
            .setContentText(text)
            .setOngoing(ongoing)
            .setOnlyAlertOnce(true)
            .setContentIntent(launch)
            .build()
    }

    private fun sanitizeFileName(name: String): String {
        return name.replace(Regex("[\\\\/:*?\"<>|]"), "_").take(120)
    }

    companion object {
        private const val CHANNEL_ID = "asz_file_download"
        private const val NOTIFICATION_ID = 7101

        const val EXTRA_DOWNLOAD_URL = "extra_download_url"
        const val EXTRA_DOWNLOAD_FILENAME = "extra_download_filename"

        fun start(context: Context, url: String, fileName: String) {
            val intent = Intent(context, FileDownloadForegroundService::class.java).apply {
                putExtra(EXTRA_DOWNLOAD_URL, url)
                putExtra(EXTRA_DOWNLOAD_FILENAME, fileName)
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }
}
