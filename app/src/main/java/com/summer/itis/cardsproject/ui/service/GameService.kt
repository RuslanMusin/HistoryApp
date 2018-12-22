package com.summer.itis.summerproject.ui.service

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.summer.itis.summerproject.R
import com.summer.itis.summerproject.model.game.GameData
import com.summer.itis.summerproject.repository.RepositoryProvider.Companion.gamesRepository
import com.summer.itis.summerproject.repository.RepositoryProvider.Companion.userRepository
import com.summer.itis.summerproject.ui.game.play.PlayGameActivity
import com.summer.itis.summerproject.ui.member.member_item.PersonalActivity
import com.summer.itis.summerproject.utils.ApplicationHelper
import com.summer.itis.summerproject.utils.Const.ONLINE_GAME
import com.summer.itis.summerproject.utils.Const.TAG_LOG
import android.app.PendingIntent
import android.support.v4.app.NotificationCompat.PRIORITY_HIGH
import com.summer.itis.summerproject.model.game.Lobby
import com.summer.itis.summerproject.ui.game.play.PlayGameActivity.Companion.ACCEPTED_GAME
import com.summer.itis.summerproject.ui.game.play.PlayGameActivity.Companion.LOBBY_JSON
import com.summer.itis.summerproject.utils.Const.gsonConverter


class GameService : Service() {

    internal val LOG_TAG = "myLogs"

    override fun onCreate() {
        super.onCreate()
        Log.d(LOG_TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(LOG_TAG, "onStartCommand")

        someTask()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(LOG_TAG, "onDestroy")
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(LOG_TAG, "onBind")
        return null
    }

    internal fun someTask() {
        Log.d(TAG_LOG,"wait enemy")
       /* gamesRepository.waitEnemy().subscribe{ lobby ->
            createNotification(lobby)
           *//* MaterialDialog.Builder(this)
                    .title(R.string.question_dialog_title)
                    .content(R.string.question_dialog_content)
                    .positiveText(R.string.agree)
                    .negativeText(R.string.disagree)
                    .onPositive(object :MaterialDialog.SingleButtonCallback {
                        override fun onClick(dialog: MaterialDialog, which: DialogAction) {
                            ApplicationHelper.currentUser?.let {
                                it.gameLobby = lobby
                                val gameData: GameData = GameData()
                                lobby.invited?.playerId?.let{
                                    gameData.enemyId = it
                                    gameData.gameMode = ONLINE_GAME
                                }
                                it.gameLobby?.gameData = gameData
                                userRepository.changeUserStatus(it).subscribe()
                                gamesRepository.acceptMyGame(lobby).subscribe{ e ->
                                    PlayGameActivity.start(this@GameService)
                                }
                            }

                        }

                    })
                    .show()*//*
        }*/
    }

    fun createNotification(lobby: Lobby) {
        val mNotifyManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager;

        val mBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_book)
                .setContentTitle("Dinner is ready!")
                .setContentText("Lentil soup, rice pilaf, and cake for dessert.")
                .setPriority(PRIORITY_HIGH)

        val contentIntent = Intent(this, PlayGameActivity::class.java)
        contentIntent.putExtra(ACCEPTED_GAME, ACCEPTED_GAME)
        contentIntent.putExtra(LOBBY_JSON, gsonConverter.toJson(lobby))
        val pendingContentIntent = PendingIntent.getActivity(this, 0,
                contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(pendingContentIntent)
//        mBuilder.addAction(R.drawable.ic_done_black_24dp, "Играть", pendingContentIntent);

        mNotifyManager.notify(0, mBuilder.build());


    }
}