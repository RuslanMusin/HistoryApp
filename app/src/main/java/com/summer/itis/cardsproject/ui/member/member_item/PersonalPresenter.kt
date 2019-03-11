package com.summer.itis.cardsproject.ui.member.member_item


import android.os.CountDownTimer
import android.util.Log

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.summer.itis.cardsproject.model.User
import com.summer.itis.cardsproject.model.db_dop_models.Relation
import com.summer.itis.cardsproject.model.game.Lobby
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.gamesRepository
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.userRepository
import com.summer.itis.cardsproject.repository.json.UserRepository
import com.summer.itis.cardsproject.ui.game.play.PlayGameActivity
import com.summer.itis.cardsproject.utils.AppHelper

import com.summer.itis.cardsproject.utils.Const.ADD_REQUEST
import com.summer.itis.cardsproject.utils.Const.IN_GAME_STATUS
import com.summer.itis.cardsproject.utils.Const.NOT_ACCEPTED
import com.summer.itis.cardsproject.utils.Const.OWNER_TYPE
import com.summer.itis.cardsproject.utils.Const.TAG_LOG
import com.summer.itis.cardsproject.utils.Const.USER_KEY

class PersonalPresenter(private val testActivity: PersonalActivity) {

    lateinit var timer: CountDownTimer

    fun setUserRelationAndView(userJson: String?) {
        if (userJson != null) {
            Log.d(TAG_LOG, "not my")
            val user = Gson().fromJson(testActivity.intent.getStringExtra(USER_KEY), User::class.java)
            testActivity.user = user
            if (user.id != UserRepository.currentId) {
                val query = user.id?.let { UserRepository().checkType(UserRepository.currentId, it) }
                val listener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val userRelation = dataSnapshot.getValue(Relation::class.java)
                            testActivity.type = userRelation!!.relation
                        } else {
                            testActivity.type = ADD_REQUEST
                        }
                        testActivity.initViews()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                }

                query?.addListenerForSingleValueEvent(listener)
            } else {
                testActivity.type = OWNER_TYPE
            }
        } else {
            testActivity.type = OWNER_TYPE
        }

        if (OWNER_TYPE == testActivity.type) {
            testActivity.initViews()
        }
    }


    fun playGame(userId: String, lobby: Lobby) {
        userRepository.checkUserStatus(userId).subscribe{ isOnline ->
            if(isOnline) {
                Log.d(TAG_LOG, "play fast game")
                gamesRepository.createFastLobby(userId,lobby).subscribe { e ->
                   gamesRepository.waitEnemy().subscribe { relation ->
                        timer.cancel()
                       if(relation.relation.equals(IN_GAME_STATUS)) {
                           AppHelper.currentUser?.let {
                               timer.cancel()
                               it.status = IN_GAME_STATUS
                               userRepository.changeUserStatus(it).subscribe()
                               PlayGameActivity.start(testActivity)
                           }
                       } else if(relation.relation.equals(NOT_ACCEPTED)) {
                           Log.d(TAG_LOG, "user not accept")
                           testActivity.hideProgressDialog()
                           testActivity.changePlayButton(true)
                           gamesRepository.removeFastLobby(userId,lobby).subscribe()
                       }
                   }
                   timer = object : CountDownTimer(20000, 1000) {

                       override fun onTick(millisUntilFinished: Long) {

                       }

                       override fun onFinish() {
                           Log.d(TAG_LOG, "user not accept")
                           testActivity.hideProgressDialog()
                           testActivity.changePlayButton(true)
                           gamesRepository.removeFastLobby(userId,lobby).subscribe()
                       }

                   }
                    timer.start()
                }
            }
        }

    }
}
