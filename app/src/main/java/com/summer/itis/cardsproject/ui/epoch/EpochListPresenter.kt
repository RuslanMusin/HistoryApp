package com.summer.itis.cardsproject.ui.epoch

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.summer.itis.cardsproject.repository.RepositoryProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

@InjectViewState
class EpochListPresenter(): MvpPresenter<EpochListView>() {

    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun loadEpoches() {

        val disposable =  RepositoryProvider.epochRepository
            .findEpoches()
            .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
            .doAfterTerminate(Action { viewState.hideLoading() })
            .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
        compositeDisposable.add(disposable)
    }
}