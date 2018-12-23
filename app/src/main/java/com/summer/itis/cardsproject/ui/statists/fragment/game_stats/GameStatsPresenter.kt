package com.summer.itis.cardsproject.ui.statists.fragment.game_stats

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.summer.itis.cardsproject.model.UserEpoch
import com.summer.itis.cardsproject.repository.RepositoryProvider
import com.summer.itis.cardsproject.ui.statists.fragment.leader_stats.LeaderStatsView
import com.summer.itis.cardsproject.utils.ApplicationHelper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

@InjectViewState
class GameStatsPresenter(): MvpPresenter<GameStatsView>() {

    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun loadStats() {

        val disposable =  RepositoryProvider.userEpochRepository
            .findUserEpoches(ApplicationHelper.currentUser.id)
            .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
            .doAfterTerminate(Action { viewState.hideLoading() })
            .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
        compositeDisposable.add(disposable)
    }
}