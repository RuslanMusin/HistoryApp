package com.summer.itis.cardsproject.ui.cards.cards_list

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.summer.itis.cardsproject.model.AbstractCard
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable

/**
 * Created by Home on 13.07.2018.
 */
@StateStrategyType(AddToEndSingleStrategy::class)
interface CardsView: MvpView{

    fun showItems(@NonNull items: List<AbstractCard>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun handleError(error: Throwable)

    fun showDetails(card: AbstractCard)

    fun showLoading(disposable: Disposable)

    fun hideLoading()
}