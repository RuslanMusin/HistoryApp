package com.summer.itis.summerproject.ui.cards.one_card_list

import android.annotation.SuppressLint
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.summer.itis.summerproject.repository.RepositoryProvider
import com.summer.itis.summerproject.repository.RepositoryProvider.Companion.cardRepository
import com.summer.itis.summerproject.ui.tests.one_test_list.OneTestListView
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

@InjectViewState
class OneCardListPresenter : MvpPresenter<OneCardListView>() {

    fun loadUserCards(userId: String) {
        cardRepository
                .findMyCards(userId)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
    }
}
