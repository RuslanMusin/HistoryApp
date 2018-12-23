package com.summer.itis.cardsproject.ui.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.summer.itis.cardsproject.repository.RepositoryProvider
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.gamesRepository
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.userRepository
import com.summer.itis.cardsproject.utils.AppHelper
import com.summer.itis.cardsproject.utils.AppHelper.Companion.offlineFunction
import com.summer.itis.cardsproject.utils.AppHelper.Companion.onlineFunction
import com.summer.itis.cardsproject.utils.AppHelper.Companion.userStatus
import com.summer.itis.cardsproject.utils.Const.OFFLINE_STATUS
import com.summer.itis.cardsproject.utils.Const.ONLINE_STATUS
import com.summer.itis.cardsproject.utils.Const.TAG_LOG

import com.summer.itis.cardsproject.utils.NetworkUtil

class NetworkChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        NetworkUtil.getConnectivityStatus(context).subscribe { isConnected ->
            if (isConnected) {
                Log.d(TAG_LOG, "changeStatus in receiver")
                RepositoryProvider.userRepository.changeJustUserStatus(userStatus).subscribe()
                gamesRepository.removeRedundantLobbies(true)
                onlineFunction?.let { it() }
                if(AppHelper.userInSession) {
                    AppHelper.currentUser.status = ONLINE_STATUS
                    Log.d(TAG_LOG, "user online")
                }
                userRepository.setOnOfflineStatus()
//                offlineFunction?.let { userRepository.checkUserConnection(it) }
            } else {
                if(AppHelper.userInSession) {
                    Log.d(TAG_LOG,"user offline")
                    AppHelper.currentUser.status = OFFLINE_STATUS
                    offlineFunction?.let { it() }
                }

            }

            NetworkUtil.getConnectivityStatusString(isConnected).subscribe { status ->


                Toast.makeText(context, status, Toast.LENGTH_LONG).show()
            }
        }
    }
}
