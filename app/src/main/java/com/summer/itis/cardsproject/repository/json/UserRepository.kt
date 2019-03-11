package com.summer.itis.cardsproject.repository.json

import android.util.Log

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.summer.itis.cardsproject.Application.Companion.TAG
import com.summer.itis.cardsproject.model.Comment
import com.summer.itis.cardsproject.model.User
import com.summer.itis.cardsproject.model.db_dop_models.ElementId
import com.summer.itis.cardsproject.model.db_dop_models.Relation
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.userEpochRepository
import com.summer.itis.cardsproject.utils.AppHelper

import java.util.ArrayList
import java.util.HashMap
import java.util.Objects

import io.reactivex.Single

import com.summer.itis.cardsproject.utils.Const.ADD_FRIEND
import com.summer.itis.cardsproject.utils.Const.OFFLINE_STATUS
import com.summer.itis.cardsproject.utils.Const.ONLINE_STATUS
import com.summer.itis.cardsproject.utils.Const.QUERY_END
import com.summer.itis.cardsproject.utils.Const.REMOVE_FRIEND
import com.summer.itis.cardsproject.utils.Const.REMOVE_REQUEST
import com.summer.itis.cardsproject.utils.Const.SEP
import com.summer.itis.cardsproject.utils.Const.TAG_LOG
import com.summer.itis.cardsproject.utils.RxUtils
import io.reactivex.Observable

class UserRepository {
    private val databaseReference: DatabaseReference

    private val CROSSING_MEMBERS = "crossing_members"
    private val USER_FRIENDS = "user_friends"
    private val USER_REQUESTS = "user_requests"


    private val FIELD_ID = "id"
    private val FIELD_NAME = "username"
    private val FIELD_LOWER_NAME = "lowerUsername"
    private val FIELD_RELATION = "relation"

    init {
        this.databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)
    }

    fun createUser(user: User) {
        databaseReference.child(user.id!!).setValue(user) { databaseError, databaseReference ->
            if (databaseError != null) {
                Log.d(TAG, "database error = " + databaseError.message)
            }
            Log.d(TAG, "completed ")
            userEpochRepository.createStartEpoches(user)
        }
    }

    fun readUser(userId: String): DatabaseReference {
        return databaseReference.child(userId)
    }

    fun readUserById(userId: String): Single<User> {
        val single:Single<User> = Single.create{e ->
            val query:Query = databaseReference.child(userId)
            query.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user: User? = dataSnapshot.getValue(User::class.java)
                    user?.let { e.onSuccess(it) }
                }

            })
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun findUsers(cardsIds: List<String>): Single<List<User>> {
        val single: Single<List<User>> = Single.create{e ->
            Observable
                    .fromIterable(cardsIds)
                    .flatMap {
                        this.readUserById(it).toObservable()
                    }
                    .toList()
                    .subscribe{users ->
                        e.onSuccess(users)
                    }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun deleteUser(user: User) {
        databaseReference.child(user.id!!).removeValue()
    }

    fun updateUser(user: User) {
        databaseReference.child(user.id).setValue(user)
    }

    fun loadDefaultUsers(): Single<List<User>> {
        val single: Single<List<User>> = Single.create { e ->
            databaseReference.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val users: MutableList<User> = ArrayList()
                    for(snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)
                        user?.let { users.add(it) }
                    }
                    e.onSuccess(users)
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun loadByCrossing(crossingId: String): Single<Query> {
        val reference = databaseReference.root.child(CROSSING_MEMBERS).child(crossingId)
        return Single.just(reference)
    }

    fun loadReadersByQuery(query: String): Single<List<User>> {
        val queryPart = query.trim { it <= ' ' }.toLowerCase()
        val single: Single<List<User>> = Single.create { e ->
            val queryName = databaseReference.orderByChild(FIELD_LOWER_NAME).startAt(queryPart).endAt(queryPart + QUERY_END)
            queryName.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val users: MutableList<User> = ArrayList()
                    for(snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)
                        user?.let { users.add(it) }
                    }
                    e.onSuccess(users)
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun loadFriendsByQuery(query: String, userId: String): Single<List<Query>> {
        val queryPart = query.trim { it <= ' ' }.toLowerCase()
        val reference = databaseReference.root.child(USER_FRIENDS).child(userId)
        return findByReference(reference, queryPart)
    }

    fun loadRequestByQuery(query: String, userId: String): Single<List<Query>> {
        val queryPart = query.trim { it <= ' ' }.toLowerCase()
        val reference = databaseReference.root.child(USER_FRIENDS).child(userId)
        return findByReference(reference, queryPart)
    }

    private fun findByReference(reference: DatabaseReference, queryPart: String): Single<List<Query>> {
        val queries = ArrayList<Query>()
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(ElementId::class.java)
                    var queryDb: Query? = databaseReference.orderByChild(FIELD_ID).equalTo(user!!.id)
                    queryDb = queryDb!!
                            .orderByChild(FIELD_NAME)
                            .startAt(queryPart)
                            .endAt(queryPart + QUERY_END)
                    if (queryDb != null) {
                        queries.add(queryDb)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
        return Single.just(queries)
    }

    fun findUsersByTypeByQuery(userQuery: String, userId: String, type: String): Single<List<User>> {
        var query: Query = databaseReference.root.child(USER_FRIENDS).child(userId).orderByChild(FIELD_RELATION).equalTo(type)
        val single: Single<List<User>> = Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val elementIds: MutableList<String> = ArrayList()
                    for (snapshot in dataSnapshot.children) {
                        val elementId = snapshot.getValue(Relation::class.java)
                        elementId?.let {
                            elementIds.add(it.id)
                        }
                    }
                    val queryPart = userQuery.toLowerCase()
                    query = databaseReference.orderByChild(FIELD_LOWER_NAME).startAt(queryPart).endAt(queryPart + QUERY_END)
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val users: MutableList<User> = ArrayList()
                            for(snapshot in dataSnapshot.children) {
                                val user = snapshot.getValue(User::class.java)
                                if(elementIds.contains(user?.id)) {
                                    user?.let { users.add(it) }
                                }

                            }
                            e.onSuccess(users)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })

                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })


        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun loadByIds(crossingsIds: List<String>): Single<List<Query>> {
        val queries = ArrayList<Query>()
        for (id in crossingsIds) {
            queries.add(databaseReference.child(id))
        }
        return Single.just(queries)
    }

    fun loadByComments(comments: List<Comment>): List<Query> {
        val queries = ArrayList<Query>()
        for (comment in comments) {
            queries.add(databaseReference.child(comment.authorId!!))
        }
        return queries
    }

    fun findUsersByIdAndType(userId: String, type: String): Single<MutableList<User>> {
        val query: Query = databaseReference.root.child(USER_FRIENDS).child(userId).orderByChild(FIELD_RELATION).equalTo(type)
        val single: Single<MutableList<User>> = Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val elementIds: MutableList<String> = ArrayList()
                    for (snapshot in dataSnapshot.children) {
                        val elementId = snapshot.getValue(Relation::class.java)
                        elementId?.let {
                            elementIds.add(it.id)
                        }
                    }
                    findUsers(elementIds).subscribe{ users ->
                        e.onSuccess(users.toMutableList())
                    }
                }

                    override fun onCancelled(databaseError: DatabaseError) {}
                    })

                }

        return single.compose(RxUtils.asyncSingle())
    }

    fun findRequests(userId: String): Single<Query> {
        val reference = databaseReference.root.child(USER_FRIENDS).child(userId).orderByChild(FIELD_RELATION)
                .equalTo(ADD_FRIEND)
        return Single.just(reference)
    }

    fun addFriend(userId: String, friendId: String) {
        val userValues = Relation.toMap(userId, REMOVE_FRIEND)
        val friendValues = Relation.toMap(friendId, REMOVE_FRIEND)
        val childUpdates = HashMap<String, Any>()
        childUpdates[USER_FRIENDS + SEP + userId + SEP + friendId] = friendValues
        childUpdates[USER_FRIENDS + SEP + friendId + SEP + userId] = userValues

        databaseReference.root.updateChildren(childUpdates)
    }

    fun removeFriend(userId: String, friendId: String) {
        val userValues = Relation.toMap(userId, REMOVE_REQUEST)
        val childUpdates = HashMap<String, Any?>()
        childUpdates[USER_FRIENDS + SEP + userId + SEP + friendId] = null
        childUpdates[USER_FRIENDS + SEP + friendId + SEP + userId] = null

        databaseReference.root.updateChildren(childUpdates)
    }

    fun addFriendRequest(userId: String, friendId: String) {
        val friendValues = Relation.toMap(friendId, REMOVE_REQUEST)
        val userValues = Relation.toMap(userId, ADD_FRIEND)
        val childUpdates = HashMap<String, Any>()
        childUpdates[USER_FRIENDS + SEP + userId + SEP + friendId] = friendValues
        childUpdates[USER_FRIENDS + SEP + friendId + SEP + userId] = userValues

        databaseReference.root.updateChildren(childUpdates)
    }

    fun removeFriendRequest(userId: String, friendId: String) {
        val childUpdates = HashMap<String, Any?>()
        childUpdates[USER_FRIENDS + SEP + userId + SEP + friendId] = null
        childUpdates[USER_FRIENDS + SEP + friendId + SEP + userId] = null

        databaseReference.root.updateChildren(childUpdates)
    }

    fun checkType(userId: String, friendId: String): Query {
        return databaseReference.root.child(USER_FRIENDS).child(userId).child(friendId)
    }

    fun changeUserStatus(user: User): Single<Boolean> {
        Log.d(TAG_LOG,"chageUserStatus = ${user.status}")
        val single: Single<Boolean> = Single.create{e ->
            user.id.let { databaseReference.child(it).child(FIELD_STATUS).setValue(user.status) }
            user.lobbyId?.let { databaseReference.root.child(GamesRepository.TABLE_LOBBIES).child(it).child(FIELD_STATUS).setValue(user.status) }
            e.onSuccess(true)
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun changeJustUserStatus(status: String): Single<Boolean> {
        Log.d(TAG_LOG,"chageJustUserStatus = $status")
        val single: Single<Boolean> = Single.create{e ->
            if(AppHelper.userInSession) {
                AppHelper.currentUser?.let { user ->
                    user.status = status
                    user.id.let { databaseReference.child(it).child(FIELD_STATUS).setValue(user.status) }
                    user.lobbyId?.let {
                        databaseReference.root.child(GamesRepository.TABLE_LOBBIES).child(it).child(FIELD_STATUS).setValue(user.status)
//                    databaseReference.root.child(GamesRepository.USERS_LOBBIES).child(user.id).child(it).child(FIELD_STATUS).setValue(user.status)
                    }
                    e.onSuccess(true)
                }
            }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun checkUserStatus(userId: String): Single<Boolean> {
        val single: Single<Boolean> = Single.create{e ->
            readUserById(userId).subscribe{user ->
                if(user.status.equals(ONLINE_STATUS)) {
                    e.onSuccess(true)
                } else {
                    e.onSuccess(false)
                }
            }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun checkUserConnection(checkIt: () -> (Unit)) {
        if(AppHelper.userInSession) {
            AppHelper.currentUser.let {
                if(it.status.equals(OFFLINE_STATUS)) {
                    checkIt()
                }
                val myConnect = databaseReference.root.child(UserRepository.TABLE_NAME).child(it.id).child(UserRepository.FIELD_STATUS)
                myConnect.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (OFFLINE_STATUS.equals(snapshot.value) || it.status.equals(OFFLINE_STATUS)) {
                            Log.d(TAG_LOG, "my disconnect")
                            checkIt()
                        }

                    }

                })
                myConnect.onDisconnect().setValue(OFFLINE_STATUS)
            }
        }
    }

    fun setOnOfflineStatus() {
        if(AppHelper.userInSession) {
            AppHelper.currentUser.let {
                val myConnect = databaseReference.root.child(UserRepository.TABLE_NAME).child(it.id).child(UserRepository.FIELD_STATUS)
                myConnect.onDisconnect().setValue(OFFLINE_STATUS)

            }
        }
    }

    companion object {

        const val TABLE_NAME = "users"

        const val FIELD_LOBBY_ID = "lobbyId"
        val FIELD_STATUS = "status"


        val currentId: String
            get() = Objects.requireNonNull<FirebaseUser>(FirebaseAuth.getInstance().currentUser).getUid()
    }
}
