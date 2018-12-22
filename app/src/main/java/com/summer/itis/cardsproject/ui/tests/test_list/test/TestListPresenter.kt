package com.summer.itis.cardsproject.ui.tests.test_list.test

import android.annotation.SuppressLint
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.summer.itis.cardsproject.model.Test
import com.summer.itis.cardsproject.repository.RepositoryProvider
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.userRepository
import com.summer.itis.cardsproject.utils.ApplicationHelper
import com.summer.itis.cardsproject.utils.Const.TAG_LOG
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

@InjectViewState
class TestListPresenter : MvpPresenter<TestListView>() {

    @SuppressLint("CheckResult")
    fun loadOfficialTestsByQUery(query: String) {
        ApplicationHelper.currentUser?.id?.let {
            RepositoryProvider.testRepository
                .findOfficialTestsByQuery(query, it)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
        }
    }

    @SuppressLint("CheckResult")
    fun loadUserTestsByQUery(query: String, userId: String) {
        RepositoryProvider.testRepository!!
                .findUserTestsByQuery(query, userId)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
    }

    @SuppressLint("CheckResult")
    fun loadMyTestsByQUery(query: String, userId: String) {
        RepositoryProvider.testRepository!!
                .findMyTestsByQuery(query, userId)
                .doOnSubscribe({ viewState.showLoading(it) })
                .doAfterTerminate({ viewState.hideLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
    }

    @SuppressLint("CheckResult")
    fun loadUserTests(userId: String) {
        RepositoryProvider.testRepository!!
                .findUserTests(userId)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
    }

    @SuppressLint("CheckResult")
    fun loadMyTests(userId: String) {
        RepositoryProvider.testRepository!!
                .findMyTests(userId)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
    }

    @SuppressLint("CheckResult")
    fun loadOfficialTests() {
        Log.d(TAG_LOG, "load books")
        ApplicationHelper.currentUser?.id?.let {
            RepositoryProvider.testRepository
                    .findOfficialTests(it)
                    .doOnSubscribe({ viewState.showLoading(it) })
                    .doAfterTerminate({ viewState.hideLoading() })
                    .doAfterTerminate({ viewState.setNotLoading() })
                    .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
        }
    }

    /*@SuppressLint("CheckResult")
    fun loadNextElements(page: Int) {
        Log.d(TAG, "load books")
        RepositoryProvider.userRepository!!
                .loadDefaultUsers()
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .doAfterTerminate(Action { viewState.setNotLoading() })
                .subscribe({ this.setReaders(it) }, { viewState.handleError(it) })

    }*/


    fun onItemClick(comics: Test) {
        viewState.showDetails(comics)
    }
}
