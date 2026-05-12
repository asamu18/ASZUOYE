package com.example.aszuoye.chat

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import java.io.BufferedOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

data class FileUploadResult(
    val success: Boolean,
    val displayName: String,
    val detail: String
)

/**
 * 使用 [AsyncTask] 在后台线程将用户选择的文件以 multipart/form-data 上传到演示接口（模拟服务器）。
 * 注意：AsyncTask 自 API 30 起已弃用，作业演示场景下保留典型用法。
 */
@Suppress("DEPRECATION")
class FileUploadAsyncTask(
    private val appContext: Context,
    private val listener: (FileUploadResult) -> Unit
) : AsyncTask<Uri, Void, FileUploadResult>() {

    override fun doInBackground(vararg params: Uri): FileUploadResult {
        val uri = params.firstOrNull() ?: return FileUploadResult(false, "", "未选择文件")
        val resolver = appContext.contentResolver
        var displayName = "upload.bin"
        resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
            if (c.moveToFirst()) {
                val idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx >= 0) displayName = c.getString(idx).orEmpty().ifBlank { displayName }
            }
        }
        val mime = resolver.getType(uri) ?: guessMime(displayName)

        val boundary = "----ASZUOYE${System.currentTimeMillis()}"
        val lineEnd = "\r\n"
        val conn = (URL(UPLOAD_URL).openConnection() as HttpURLConnection).apply {
            doOutput = true
            requestMethod = "POST"
            setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            connectTimeout = 25_000
            readTimeout = 60_000
        }

        return try {
            val input = resolver.openInputStream(uri)
                ?: return FileUploadResult(false, displayName, "无法读取文件")
            input.use { stream ->
                BufferedOutputStream(conn.outputStream).use { out ->
                    val header = buildString {
                        append("--").append(boundary).append(lineEnd)
                        append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                        append(displayName.replace("\"", "'"))
                        append("\"").append(lineEnd)
                        append("Content-Type: ").append(mime).append(lineEnd)
                        append(lineEnd)
                    }
                    out.write(header.toByteArray(Charsets.UTF_8))
                    copyStream(stream, out)
                    val footer = "$lineEnd--$boundary--$lineEnd"
                    out.write(footer.toByteArray(Charsets.UTF_8))
                    out.flush()
                }
            }
            val code = conn.responseCode
            val ok = code in 200..299
            val body = (if (ok) conn.inputStream else conn.errorStream)?.bufferedReader()?.use { it.readText() }
                .orEmpty()
            val snippet = body.take(200)
            FileUploadResult(ok, displayName, if (ok) "HTTP $code" else "HTTP $code $snippet")
        } catch (e: Exception) {
            Log.w(TAG, "upload", e)
            FileUploadResult(false, displayName, e.message ?: "上传异常")
        } finally {
            conn.disconnect()
        }
    }

    override fun onPostExecute(result: FileUploadResult) {
        listener(result)
    }

    private fun copyStream(input: InputStream, out: BufferedOutputStream) {
        val buf = ByteArray(8192)
        var n: Int
        while (input.read(buf).also { n = it } != -1) {
            if (isCancelled) return
            out.write(buf, 0, n)
        }
    }

    private fun guessMime(fileName: String): String {
        val ext = fileName.substringAfterLast('.', "").lowercase(Locale.getDefault())
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
            ?: "application/octet-stream"
    }

    companion object {
        private const val TAG = "FileUploadAsyncTask"
        /** 演示用：接收 multipart 并回显表单字段 */
        private const val UPLOAD_URL = "https://httpbin.org/post"
    }
}
