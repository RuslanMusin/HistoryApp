package com.summer.itis.cardsproject.ui.tests.test_item.fragments.main

import com.arellomobile.mvp.MvpView
import com.summer.itis.cardsproject.model.Comment
import io.reactivex.disposables.Disposable

interface TestFragmentView : MvpView {

    fun showComments(comments: List<Comment>)

    fun addComment(comment: Comment)

    fun setData()

    fun showLoading(disposable: Disposable)

    fun hideLoading()



}


