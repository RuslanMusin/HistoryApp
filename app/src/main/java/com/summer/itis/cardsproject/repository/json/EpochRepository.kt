package com.summer.itis.cardsproject.repository.json

import android.util.Log
import com.google.firebase.database.*
import com.summer.itis.cardsproject.model.Epoch
import com.summer.itis.cardsproject.model.UserEpoch
import com.summer.itis.cardsproject.utils.Const
import com.summer.itis.cardsproject.utils.RxUtils
import io.reactivex.Single

class EpochRepository {

    var databaseReference: DatabaseReference

    val TABLE_NAME = "epoches"

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

    fun findEpoch(id: String): Single<Epoch> {
        val single: Single<Epoch> = Single.create{ e ->
            val query: Query = databaseReference.child(id)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val epoch = dataSnapshot.getValue(Epoch::class.java)
                    epoch?.let { e.onSuccess(it) }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(Const.TAG_LOG, "loadPost:onCancelled", databaseError.toException())
                }
            })

        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun findEpoches(): Single<List<Epoch>> {
        val single: Single<List<Epoch>> = Single.create{ e ->
            val query: Query = databaseReference
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val comments: MutableList<Epoch> = ArrayList()
                    for (postSnapshot in dataSnapshot.children) {
                        comments.add(postSnapshot.getValue(Epoch::class.java)!!)
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

    fun createEpoch(epoch: Epoch): Single<Boolean> {
        val single: Single<Boolean> = Single.create { e ->

            val key = databaseReference.push().key
            key?.let { epoch.id = it }
            databaseReference.child(epoch.id).setValue(epoch)
            e.onSuccess(true)
            /*query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(!dataSnapshot.exists()) {
                        databaseReference.child(userEpoch.userId).child(userEpoch.id).setValue(userEpoch)
                        e.onSuccess(true)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(Const.TAG_LOG, "loadPost:onCancelled", databaseError.toException())
                }
            })*/

        }
        return single.compose(RxUtils.asyncSingle())
    }
}