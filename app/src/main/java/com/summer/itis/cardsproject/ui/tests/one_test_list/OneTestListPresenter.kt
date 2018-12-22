package com.summer.itis.summerproject.ui.tests.one_test_list

import android.annotation.SuppressLint
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.summer.itis.summerproject.repository.RepositoryProvider
import com.summer.itis.summerproject.ui.tests.test_list.test.TestListView
import com.summer.itis.summerproject.utils.ApplicationHelper
import com.summer.itis.summerproject.utils.Const.TAG_LOG
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

@InjectViewState
class OneTestListPresenter : MvpPresenter<OneTestListView>() {

    @SuppressLint("CheckResult")
    fun loadUserTests(userId: String) {
        RepositoryProvider.testRepository
                .findMyTests(userId)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
    }

    @SuppressLint("CheckResult")
    fun loadDefaultAbstractCardTests(abstractCardId: String) {
        RepositoryProvider.cardRepository
                .findDefaultAbstractCardTests(abstractCardId)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
    }

    @SuppressLint("CheckResult")
    fun loadMyAbstractCardTests(abstractCardId: String,userId: String) {
        RepositoryProvider.cardRepository
                .findMyAbstractCardTests(abstractCardId, userId)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
    }
}
