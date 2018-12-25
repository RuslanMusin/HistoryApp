package com.summer.itis.cardsproject.ui.statists.fragment.leader_stats

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.summer.itis.cardsproject.model.LeaderStat
import com.summer.itis.cardsproject.repository.RepositoryProvider
import com.summer.itis.cardsproject.utils.AppHelper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

@InjectViewState
class LeaderStatsPresenter(): MvpPresenter<LeaderStatsView>() {

    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun loadStats() {
        val disposable =  RepositoryProvider.leaderStatRepository
            .findStats(AppHelper.currentUser)
            .map { stats -> stats.sortedWith(compareByDescending(LeaderStat::kg)) }
            .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
            .doAfterTerminate(Action { viewState.hideLoading() })
            .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
        compositeDisposable.add(disposable)
    }
}