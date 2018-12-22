package com.summer.itis.summerproject.ui.cards.card_item

import com.arellomobile.mvp.MvpView
import com.summer.itis.summerproject.model.Comment
import io.reactivex.disposables.Disposable

interface CardView : MvpView {

    fun showComments(comments: List<Comment>)

    fun addComment(comment: Comment)

    fun showLoading(disposable: Disposable)

    fun hideLoading()

}