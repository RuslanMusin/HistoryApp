package com.summer.itis.summerproject.ui.cards.add_card_list

import android.annotation.SuppressLint
import android.util.Log

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.summer.itis.summerproject.model.pojo.opensearch.Item
import com.summer.itis.summerproject.repository.RepositoryProvider

import com.summer.itis.summerproject.utils.Const.TAG_LOG
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

@InjectViewState
class AddCardListPresenter : MvpPresenter<AddCardListView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d(TAG_LOG, "attach listPresenter")
    }

    @SuppressLint("CheckResult")
    fun opensearch(opensearch: String) {
        Log.d(TAG_LOG,"pres opensearch")
        RepositoryProvider.wikiApiRepository
                .opensearch(opensearch)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe({ viewState.setOpenSearchList(it) }, { viewState.handleError(it) })
    }
}
