package com.summer.itis.cardsproject.repository.json

import android.util.Log
import com.google.firebase.database.*
import com.summer.itis.cardsproject.model.LeaderStat
import com.summer.itis.cardsproject.model.User
import com.summer.itis.cardsproject.model.UserEpoch
import com.summer.itis.cardsproject.model.game.Lobby
import com.summer.itis.cardsproject.model.game.LobbyData
import com.summer.itis.cardsproject.repository.RepositoryProvider
import com.summer.itis.cardsproject.utils.AppHelper
import com.summer.itis.cardsproject.utils.Const
import com.summer.itis.cardsproject.utils.RxUtils
import io.reactivex.Single

class GameFakeRepository {

    var databaseReference: DatabaseReference

    val TABLE_NAME = "lobby_data"

    private val FIELD_ID = "id"
    private val FIELD_USER_ID = "userId"
    private val FIELD_EPOCH_ID = "epochId"
    private val FIELD_SUM = "sum"
    private val FIELD_WIN = "win"
    private val FIELD_LOSE = "lose"
    private val FIELD_GE = "ge"
    private val FIELD_LAST_GE = "lastGe"
    private val FIELD_GE_SUB = "geSub"
    private val FIELD_RIGHT = "right"
    private val FIELD_WRONG = "wrong"
    private val FIELD_KE = "ke"
    private val FIELD_LAST_KE = "lastKe"
    private val FIELD_KE_SUB = "keSub"
    private val FIELD_UPDATE_DATE = "updateDate"



    init {
        this.databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)
    }

    fun toMap(card: UserEpoch?): Map<String, Any?> {
        val result = HashMap<String, Any?>()

        card?.let {
            result[FIELD_ID] = card.id
            result[FIELD_USER_ID] = card.userId
            result[FIELD_EPOCH_ID] = card.epochId
            result[FIELD_SUM] = card.sum
            result[FIELD_WIN] = card.win
            result[FIELD_LOSE] = card.lose

            result[FIELD_GE] = card.ge
            result[FIELD_LAST_GE] = card.lastGe
            result[FIELD_GE_SUB] = card.geSub
            result[FIELD_RIGHT] = card.right
            result[FIELD_WRONG] = card.wrong
            result[FIELD_KE] = card.ke
            result[FIELD_LAST_KE] = card.lastKe
            result[FIELD_KE_SUB] = card.keSub
            result[FIELD_UPDATE_DATE] = card.updateDate
        }
        return result
    }

    fun toMapId( value: String?): Map<String, Any?> {
        val result = HashMap<String, Any?>()
        result[FIELD_ID] = value
        return result
    }


    fun setDatabaseReference(path: String) {
        this.databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)
    }

    fun getKey(crossingId: String): String? {
        return databaseReference!!.child(crossingId).push().key
    }

    fun findUserEpoch(userId: String, epochId: String): Single<UserEpoch> {
        val single: Single<UserEpoch> = Single.create{ e ->
            val query: Query = databaseReference.child(userId).child(epochId)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val epoch: UserEpoch = dataSnapshot.getValue(UserEpoch::class.java)!!
                    e.onSuccess(epoch)

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(Const.TAG_LOG, "loadPost:onCancelled", databaseError.toException())
                }
            })

        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun findUserEpoches(userId: String): Single<List<UserEpoch>> {
        val single: Single<List<UserEpoch>> = Single.create{ e ->
            val query: Query = databaseReference.child(userId)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val comments: MutableList<UserEpoch> = ArrayList()
                    for (postSnapshot in dataSnapshot.children) {
                        comments.add(postSnapshot.getValue(UserEpoch::class.java)!!)
                    }
                    e.onSuccess(comments)

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(Const.TAG_LOG, "loadPost:onCancelled", databaseError.toException())
                }
            })

        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun updateLobby(lobbyData: LobbyData): Single<Boolean> {
        val single: Single<Boolean> = Single.create { e ->

        /*    val key = databaseReference.child(userEpoch.userId).push().key
            key?.let { userEpoch.id = it }*/
            val ref : DatabaseReference = databaseReference.child(lobbyData.id)
            ref.setValue(lobbyData).addOnCompleteListener { lis ->
                e.onSuccess(true)
            }

        }
        return single.compose(RxUtils.asyncSingle())
    }

}
