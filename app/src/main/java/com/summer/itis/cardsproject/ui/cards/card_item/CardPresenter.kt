package com.summer.itis.summerproject.ui.cards.card_item

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.summer.itis.summerproject.model.Comment
import com.summer.itis.summerproject.repository.RepositoryProvider.Companion.cardCommentRepository
import com.summer.itis.summerproject.repository.RepositoryProvider.Companion.testCommentRepository
import com.summer.itis.summerproject.ui.tests.test_item.TestView
import com.summer.itis.summerproject.utils.Const.TAG_LOG
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

@InjectViewState
class CardPresenter : MvpPresenter<CardView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d(TAG_LOG, "attach listPresenter")
    }

    fun loadComments(crossingId: String) {
        cardCommentRepository.getComments(crossingId)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe{ comments ->
                    viewState?.showComments(comments)
                }
    }

    fun createComment(crossingId: String, comment: Comment) {
        cardCommentRepository.createComment(crossingId,comment)
                .subscribe()
    }

}