package com.example.aszuoye

data class Msg(
    val content: String,
    val type: Int,
    val time: String = ""
) {
    companion object {
        const val TYPE_RECEIVED = 0
        const val TYPE_SENT = 1
    }
}
