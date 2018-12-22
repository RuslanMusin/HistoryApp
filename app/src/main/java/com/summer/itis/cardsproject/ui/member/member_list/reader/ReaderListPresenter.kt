package com.summer.itis.summerproject.ui.member.member_list.reader

import android.annotation.SuppressLint
import android.util.Log

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.summer.itis.summerproject.model.User
import com.summer.itis.summerproject.model.db_dop_models.ElementId
import com.summer.itis.summerproject.repository.RepositoryProvider

import java.util.ArrayList

import android.support.constraint.Constraints.TAG
import com.summer.itis.summerproject.utils.Const.TAG_LOG
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer


@InjectViewState
class ReaderListPresenter : MvpPresenter<ReaderListView>() {

    @SuppressLint("CheckResult")
    fun loadReadersByQuery(query: String) {
        RepositoryProvider.userRepository
                .loadReadersByQuery(query)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe({ viewState.changeDataSet(it.toMutableList()) }, { viewState.handleError(it) })
    }

    @SuppressLint("CheckResult")
    fun loadUsersByQueryAndType(query: String, userId: String, type: String) {
        RepositoryProvider.userRepository!!
                .findUsersByTypeByQuery(query, userId, type)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe({ viewState.changeDataSet(it.toMutableList()) }, { viewState.handleError(it) })
    }

   /* @SuppressLint("CheckResult")
    fun loadRequestByQuery(query: String, userId: String) {
        RepositoryProvider.userRepository!!
                .loadRequestByQuery(query, userId)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe({ this.setRequestsByQuery(it) }, { viewState.handleError(it) })
    }
*/
    @SuppressLint("CheckResult")
    fun loadUsers(userId: String, type: String) {
        RepositoryProvider.userRepository!!
                .findUsersByIdAndType(userId, type)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe( {viewState.changeDataSet(it)}, { viewState.handleError(it) })
    }

    @SuppressLint("CheckResult")
    fun loadReaders() {
        Log.d(TAG, "load books")
        RepositoryProvider.userRepository!!
                .loadDefaultUsers()
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .doAfterTerminate(Action { viewState.setNotLoading() })
                .subscribe({ viewState.changeDataSet(it.toMutableList()) }, { viewState.handleError(it) })
    }

    @SuppressLint("CheckResult")
    fun loadNextElements(page: Int) {
        Log.d(TAG, "load books")
        RepositoryProvider.userRepository!!
                .loadDefaultUsers()
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .doAfterTerminate(Action { viewState.setNotLoading() })
                .subscribe({  viewState.changeDataSet(it.toMutableList()) }, { viewState.handleError(it) })

    }

    @SuppressLint("CheckResult")
    fun loadByIds(usersId: List<String>) {
        RepositoryProvider.userRepository!!
                .loadByIds(usersId)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe({ this.showItems(it) }, { viewState.handleError(it) })
    }


    //work with DB
    fun setFriends(books: Query) {
        books.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val friendsId = ArrayList<String>()
                for (snapshot in dataSnapshot.children) {
                    val elementId = snapshot.getValue(ElementId::class.java)
                    friendsId.add(elementId!!.id)
                }
                loadByIds(friendsId)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    fun setRequests(books: Query) {
        books.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val friendsId = ArrayList<String>()
                for (snapshot in dataSnapshot.children) {
                    val elementId = snapshot.getValue(ElementId::class.java)
                    friendsId.add(elementId!!.id)
                }
                loadByIds(friendsId)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    fun setReaders(books: Query) {
        books.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val users = ArrayList<User>()
                for (snapshot in dataSnapshot.children) {
                    val reader = snapshot.getValue(User::class.java)
                    if (reader != null) {
                        users.add(reader)
                    }
                }
                viewState.changeDataSet(users)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    fun setFriendsByQuery(queries: List<Query>) {
        val friends = ArrayList<User>()
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val reader = snapshot.getValue(User::class.java)
                    reader?.let { friends.add(it) }
                    if (friends.size == queries.size) {
                        viewState.changeDataSet(friends)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        for (query in queries) {
            query.addListenerForSingleValueEvent(listener)
        }

    }

    fun setRequestsByQuery(queries: List<Query>) {
        val requests = ArrayList<User>()
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val reader = snapshot.getValue(User::class.java)
                    reader?.let { requests.add(it) }
                    if (requests.size == queries.size) {
                        viewState.changeDataSet(requests)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        for (query in queries) {
            query.addListenerForSingleValueEvent(listener)
        }

    }

    fun showItems(queries: List<Query>) {
        val users = ArrayList<User>()
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val crossing = dataSnapshot.getValue(User::class.java)
                crossing?.let { users.add(it) }
                if (users.size == queries.size) {
                    viewState.changeDataSet(users)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG_LOG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        for (query in queries) {
            query.addListenerForSingleValueEvent(listener)
        }
    }


    fun onItemClick(comics: User) {
        viewState.showDetails(comics)
    }
}
