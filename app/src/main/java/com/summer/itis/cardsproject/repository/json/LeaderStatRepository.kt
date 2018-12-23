package com.summer.itis.cardsproject.repository.json

import android.util.Log
import com.google.firebase.database.*
import com.summer.itis.cardsproject.model.LeaderStat
import com.summer.itis.cardsproject.model.User
import com.summer.itis.cardsproject.utils.Const
import com.summer.itis.cardsproject.utils.RxUtils
import io.reactivex.Single

class LeaderStatRepository {

    var databaseReference: DatabaseReference

    val TABLE_NAME = "leader_stats"
    val USERS_CARDS = "users_cards"
    val USERS_TESTS = "users_tests"
    val USERS_ABSTRACT_CARDS = "users_abstract_cards"


    private val FIELD_ID = "id"
    private val FIELD_NAME = "name"
    private val FIELD_LEVEL = "level"
    private val FIELD_KG = "kg"
    private val FIELD_WIN = "win"
    private val FIELD_LOSE = "lose"

    init {
        this.databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)
    }

    fun toMap(card: LeaderStat?): Map<String, Any?> {
        val result = HashMap<String, Any?>()

        card?.let {
            result[FIELD_ID] = card.id
            result[FIELD_NAME] = card.name
            result[FIELD_LEVEL] = card.level
            result[FIELD_KG] = card.kg
            result[FIELD_WIN] = card.win
            result[FIELD_LOSE] = card.lose
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

  /*  fun readCard(cardId: String): Single<Card> {
        var card: Le?
        val query: Query = databaseReference.child(cardId)
        val single : Single<Card> = Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    card = dataSnapshot.getValue(Card::class.java)
                    AbstractCardRepository()
                        .findAbstractCard(card?.cardId)
                        .subscribe { t ->
                            card?.abstractCard = t
                            TestRepository()
                                .readTest(card?.testId)
                                .subscribe{ test ->
                                    card?.test = test
                                    e.onSuccess(card!!)
                                }
                        }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        }
        return single.compose(RxUtils.asyncSingle())
    }*/

  /*  fun readCardForTest(cardId: String): Single<Card> {
        var card: Card?
        val query: Query = databaseReference.child(cardId)
        val single : Single<Card> = Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    card = dataSnapshot.getValue(Card::class.java)
                    AbstractCardRepository()
                        .findAbstractCard(card?.cardId)
                        .subscribe { t ->
                            card?.abstractCard = t
                            e.onSuccess(card!!)
                        }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        }
        return single.compose(RxUtils.asyncSingle())
    }*/

    fun findStats(user: User): Single<List<LeaderStat>> {
        val single: Single<List<LeaderStat>> = Single.create{e ->
            val query: Query = databaseReference.orderByChild(FIELD_KG)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val comments: MutableList<LeaderStat> = ArrayList()
                    for (postSnapshot in dataSnapshot.children) {
                        val stat = postSnapshot.getValue(LeaderStat::class.java)!!
                        if(Math.abs((user.level - stat.level)) <= 3) {
                            comments.add(stat)
                        }
                        if(comments.size == 100) {
                            break
                        }
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

    fun updateLeaderStat(stat: LeaderStat): Single<Boolean> {
        val single: Single<Boolean> = Single.create { e ->
            val query: Query = databaseReference.child(stat.id)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    databaseReference.child(stat.id).setValue(stat)
                    e.onSuccess(true)

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(Const.TAG_LOG, "loadPost:onCancelled", databaseError.toException())
                }
            })

        }
        return single.compose(RxUtils.asyncSingle())
    }

  /*  fun findMyCards(userId: String): Single<List<Card>> {
        return Single.create { e ->
            val query: Query = databaseReference.root.child(USERS_CARDS).child(userId)
            query.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val elementIds: MutableList<String> = ArrayList()
                    for (snapshot in dataSnapshot.children) {
                        val elementId = snapshot.getValue(ElementId::class.java)
                        elementId?.let { elementIds.add(it.id) }
                    }
                    findCards(elementIds).subscribe{ cards ->
                        e.onSuccess(cards)
                    }

                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
        }
    }*/
}
