package com.summer.itis.summerproject.utils

import android.content.Context
import android.net.ConnectivityManager
import android.os.CountDownTimer
import io.reactivex.Single

object NetworkUtil {

    var TYPE_WIFI = 1
    var TYPE_MOBILE = 2
    var TYPE_NOT_CONNECTED = 0


    lateinit var netTimer: CountDownTimer

    fun getConnectivityStatus(context: Context): Single<Boolean> {
        return Single.create { e ->
            val cm = context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var isConnected = false

            netTimer = object : CountDownTimer(5000, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    val activeNetwork = cm.activeNetworkInfo
                    if (activeNetwork != null) {
                        isConnected = activeNetwork.isConnectedOrConnecting
                        netTimer.cancel()
                        e.onSuccess(isConnected)
                    }

                }

                override fun onFinish() {
                    val activeNetwork = cm.activeNetworkInfo
                    if (activeNetwork != null) {
                        isConnected = activeNetwork.isConnectedOrConnecting
                        netTimer.cancel()
                    }
                    e.onSuccess(isConnected)
                }
            }
            netTimer.start()
        }

    }

    fun getConnectivityStatusString(isConnected: Boolean): Single<String> {
        return Single.create { e ->
                var status: String = "Not connected to Internet"
                if (isConnected == true) {
                    status = "Has connection"
                }
                e.onSuccess(status)
        }
    }
}