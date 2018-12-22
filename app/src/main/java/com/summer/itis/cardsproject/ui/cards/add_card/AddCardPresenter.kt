package com.summer.itis.summerproject.ui.cards.add_card

import android.annotation.SuppressLint
import android.util.Log

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.summer.itis.summerproject.model.pojo.query.Page
import com.summer.itis.summerproject.repository.RepositoryProvider
import com.summer.itis.summerproject.ui.tests.add_test.AddTestView

import com.summer.itis.summerproject.utils.Const.TAG_LOG
import io.reactivex.functions.Consumer

@InjectViewState
class AddCardPresenter : MvpPresenter<AddCardView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d(TAG_LOG, "attach listPresenter")
    }

    @SuppressLint("CheckResult")
    fun query(query: String) {
        RepositoryProvider.wikiApiRepository!!
                .query(query)
                .subscribe({ viewState.setQueryResults(it) }, { viewState.handleError(it) })

    }
}
