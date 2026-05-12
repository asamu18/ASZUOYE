package com.example.aszuoye.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper

/**
 * 使用 [ConnectivityManager.registerDefaultNetworkCallback] 在运行时动态注册，
 * 监听默认网络可用 / 丢失 / 能力变化（无需在 Manifest 中声明 receiver）。
 */
class NetworkConnectivityHelper(
    context: Context,
    private val onNetworkEvent: (message: String) -> Unit
) {
    private val appContext = context.applicationContext
    private val connectivityManager =
        appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val mainHandler = Handler(Looper.getMainLooper())

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            post("网络已连接")
        }

        override fun onLost(network: Network) {
            post("网络已断开")
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            val type = when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi‑Fi"
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "移动数据"
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "以太网"
                else -> "其他链路"
            }
            post("当前网络类型：$type")
        }
    }

    private fun post(message: String) {
        mainHandler.post { onNetworkEvent(message) }
    }

    fun register() {
        connectivityManager.registerDefaultNetworkCallback(callback)
    }

    fun unregister() {
        connectivityManager.unregisterNetworkCallback(callback)
    }
}
