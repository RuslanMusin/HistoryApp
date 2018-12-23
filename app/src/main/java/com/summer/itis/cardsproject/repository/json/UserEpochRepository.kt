package com.summer.itis.cardsproject.repository.json

import android.util.Log
import com.google.firebase.database.*
import com.summer.itis.cardsproject.model.*
import com.summer.itis.cardsproject.model.game.LobbyData
import com.summer.itis.cardsproject.repository.RepositoryProvider
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.epochRepository
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.leaderStatRepository
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.userRepository
import com.summer.itis.cardsproject.utils.AppHelper
import com.summer.itis.cardsproject.utils.Const
import com.summer.itis.cardsproject.utils.Const.GAME_LOSE_POINTS
import com.summer.itis.cardsproject.utils.Const.GAME_WIN_POINTS
import com.summer.itis.cardsproject.utils.Const.TAG_LOG
import com.summer.itis.cardsproject.utils.RxUtils
import io.reactivex.Single

class UserEpochRepository {

    var databaseReference: DatabaseReference

    val TABLE_NAME = "users_epoches"

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

    fun findUserEpoch(userId: String, epochId: String): Single<UserEpoch> {
        val single: Single<UserEpoch> = Single.create{ e ->
            val query: Query = databaseReference.child(userId).child(epochId)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val epoch: UserEpoch = dataSnapshot.getValue(UserEpoch::class.java)!!
                    epochRepository.findEpoch(epoch.epochId).subscribe { ep ->
                        epoch.epoch = ep
                        e.onSuccess(epoch)
                    }

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
            epochRepository.findEpoches().subscribe { epoches ->
                val query: Query = databaseReference.child(userId)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val comments: MutableList<UserEpoch> = ArrayList()
                        for (postSnapshot in dataSnapshot.children) {
                            val userEpoch = postSnapshot.getValue(UserEpoch::class.java)!!
                            userEpoch.epoch = findEpochById(epoches, userEpoch.epochId)
                            comments.add(userEpoch)
                        }
                        e.onSuccess(comments)

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.d(Const.TAG_LOG, "loadPost:onCancelled", databaseError.toException())
                    }
                })
            }


        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun findEpochById(epoches: List<Epoch>, epochId: String): Epoch {
        for(ep in epoches) {
            if(ep.id.equals(epochId)) {
                return ep
            }
        }
        return Epoch()
    }

    fun updateUserEpoch(userEpoch: UserEpoch): Single<Boolean> {
        val single: Single<Boolean> = Single.create { e ->

            val ref : DatabaseReference = databaseReference.child(userEpoch.userId).child(userEpoch.id)
            ref.setValue(userEpoch).addOnCompleteListener { lis ->
                e.onSuccess(true)
            }
           /* query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    databaseReference.child(userEpoch.userId).child(userEpoch.id).setValue(userEpoch)
                    e.onSuccess(true)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(Const.TAG_LOG, "loadPost:onCancelled", databaseError.toException())
                }
            })*/

        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun createStartEpoches(user: User) {
        epochRepository.findEpoches().subscribe { epoches ->
            for(item in epoches) {
                updateUserEpoch(UserEpoch(item, user)).subscribe { e ->
                    Log.d(TAG_LOG, "updated user epoch ${item.name}")
                }
            }
        }

    }

    fun updateAfterGame(lobby: LobbyData, playerId: String?, isWin: Boolean, score: Int) {
        playerId?.let {
            userRepository.readUserById(it).subscribe { user ->
                findUserEpoch(it, lobby.epochId).subscribe { epoch ->
                    if(isWin) {
                        epoch.win++
                        user.points += GAME_WIN_POINTS
                    } else {
                        epoch.lose++
                        user.points += GAME_LOSE_POINTS
                    }
                    if(user.points >= user.nextLevel) {
                        user.nextLevel = (1.5 * user.points + 20 * user.level).toLong()
                        user.level++
                    }
                    epoch.updateGe()
                    userRepository.updateUser(user)
                    if(user.id.equals(AppHelper.currentUser.id)) {
                        AppHelper.currentUser = user
                    }
                    epoch.right.plus(score)
                    epoch.wrong.plus(lobby.cardNumber - score)
                    updateUserEpoch(epoch).subscribe { e ->
                        findUserEpoches(playerId).subscribe {epoches ->
                            user.epochList = epoches.toMutableList()
                            val leaderStat = LeaderStat(user)
                            leaderStatRepository.updateLeaderStat(leaderStat).subscribe()
                        }
                    }
                }

            }
        }
    }

    fun updateAfterTest(userId: String, test: Test): Single<Boolean> {
        val single: Single<Boolean> = Single.create { e ->
            findUserEpoch(userId, test.epochId).subscribe { userEpoch ->
                Log.d(TAG_LOG, "epoch was finded")
                val right = test.rightQuestions.size
                val wrong = test.wrongQuestions.size
                userEpoch.right += right
                userEpoch.wrong += wrong
                userEpoch.ke += ((right - wrong).toDouble() / (right + wrong))
                Log.d(TAG_LOG, "userEpoch.ke = ${userEpoch.ke}")
                updateUserEpoch(userEpoch).subscribe { flag  -> e.onSuccess(true)}
            }
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
