package com.summer.itis.summerproject.ui.cards.add_card_list

import com.arellomobile.mvp.MvpView
import com.summer.itis.summerproject.model.pojo.opensearch.Item
import com.summer.itis.summerproject.model.pojo.query.Page
import io.reactivex.disposables.Disposable

interface AddCardListView : MvpView {
    fun setOpenSearchList(list: List<Item>)

    fun handleError(throwable: Throwable)

    fun showLoading(disposable: Disposable)

    fun hideLoading()
}

