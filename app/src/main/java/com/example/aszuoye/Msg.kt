package com.example.aszuoye

data class Msg(
    val content: String,
    val type: Int,
    val time: String = "",
    /** 右侧气泡展示为「文件」样式（上传完成后） */
    val isFile: Boolean = false
) {
    companion object {
        const val TYPE_RECEIVED = 0
        const val TYPE_SENT = 1
    }
}
