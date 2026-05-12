package com.example.aszuoye.broadcast

/**
 * 自定义广播：模拟服务端下发「账号在别处登录」等强制下线（类似 QQ）。
 * 发送示例（adb）：
 * adb shell am broadcast -a com.example.aszuoye.ACTION_FORCE_LOGOUT --es extra_message "账号在别处登录" com.example.aszuoye
 */
object AppBroadcasts {
    const val ACTION_FORCE_LOGOUT = "com.example.aszuoye.ACTION_FORCE_LOGOUT"
    const val EXTRA_MESSAGE = "extra_message"
}
