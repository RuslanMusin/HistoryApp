package com.summer.itis.cardsproject.ui.statists.fragment.common_stats

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.summer.itis.cardsproject.model.UserEpoch
import com.summer.itis.cardsproject.repository.RepositoryProvider
import com.summer.itis.cardsproject.utils.AppHelper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

@InjectViewState
class CommonStatsPresenter(): MvpPresenter<CommonStatsView>() {

    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun loadStats() {
        val disposable =  RepositoryProvider.userEpochRepository
            .findUserEpoches(AppHelper.currentUser.id)
            .map { epoches -> epoches.sortedWith(compareByDescending(UserEpoch::keSub)) }
            .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
            .doAfterTerminate(Action { viewState.hideLoading() })
            .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
        compositeDisposable.add(disposable)
    }
}