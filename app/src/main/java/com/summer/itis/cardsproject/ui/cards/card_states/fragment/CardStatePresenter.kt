package com.summer.itis.summerproject.ui.cards.card_states.fragment

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.summer.itis.summerproject.model.Test
import com.summer.itis.summerproject.repository.RepositoryProvider
import com.summer.itis.summerproject.repository.RepositoryProvider.Companion.testRepository
import com.summer.itis.summerproject.utils.Const.TAG_LOG

@InjectViewState
class CardStatePresenter : MvpPresenter<CardStateView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d(TAG_LOG, "attach listPresenter")
    }

    fun readTestName(testId: String) {
        testRepository.readTest(testId).subscribe{test ->
            viewState.setTestData(test)
        }
    }
}