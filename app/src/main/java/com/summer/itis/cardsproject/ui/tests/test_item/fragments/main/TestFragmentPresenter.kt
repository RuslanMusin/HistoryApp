package com.summer.itis.summerproject.ui.tests.test_item.fragments.main

import android.annotation.SuppressLint
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.summer.itis.summerproject.model.Comment
import com.summer.itis.summerproject.model.Test
import com.summer.itis.summerproject.repository.RepositoryProvider.Companion.cardRepository
import com.summer.itis.summerproject.repository.RepositoryProvider.Companion.testCommentRepository
import com.summer.itis.summerproject.utils.Const
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

@InjectViewState
class TestFragmentPresenter : MvpPresenter<TestFragmentView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d(Const.TAG_LOG, "attach listPresenter")
    }


    fun loadComments(crossingId: String) {
        testCommentRepository.getComments(crossingId)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe{ comments ->
                    viewState?.showComments(comments)
                }
    }

    fun createComment(crossingId: String, comment: Comment) {
        testCommentRepository.createComment(crossingId,comment)
                .subscribe()
    }

    fun readCardForTest(test: Test) {
        test.cardId?.let {
            cardRepository.readCardForTest(it).subscribe{ card ->
                test.card = card
                viewState.setData()
            }
        }
    }

}