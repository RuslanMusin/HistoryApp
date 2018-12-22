package com.summer.itis.cardsproject.ui.cards.one_card_list

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.summer.itis.cardsproject.model.AbstractCard
import com.summer.itis.cardsproject.model.Card
import com.summer.itis.cardsproject.model.Test
import com.summer.itis.cardsproject.ui.base.BaseAdapter
import io.reactivex.disposables.Disposable

interface OneCardListView : MvpView, BaseAdapter.OnItemClickListener<AbstractCard> {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun handleError(throwable: Throwable)

    fun setNotLoading()

    fun showLoading(disposable: Disposable)

    fun hideLoading()

    fun loadNextElements(i: Int)

    fun changeDataSet(friends: List<Card>)
}