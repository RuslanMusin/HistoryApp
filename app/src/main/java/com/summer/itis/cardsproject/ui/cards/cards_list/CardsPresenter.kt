package com.summer.itis.summerproject.ui.cards.cards_list

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.summer.itis.summerproject.model.AbstractCard
import com.summer.itis.summerproject.repository.RepositoryProvider
import com.summer.itis.summerproject.utils.ApplicationHelper
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * Created by Home on 13.07.2018.
 */
@InjectViewState
open class CardsPresenter: MvpPresenter<CardsView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }

    fun getAbstractCardsList(){
        ApplicationHelper.currentUser?.id?.let{it ->
            RepositoryProvider
                    .abstractCardRepository?.findDefaultAbstractCards(it)
                    .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                    .doAfterTerminate(Action { viewState.hideLoading() })
                    .subscribe(viewState::showItems,viewState::handleError)
        }
    }

    fun getUserAbstractCardsList(){
        ApplicationHelper.currentUser?.id?.let{it ->
            RepositoryProvider
                    .abstractCardRepository?.findMyAbstractCards(it)
                    .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                    .doAfterTerminate(Action { viewState.hideLoading() })
                    .subscribe(viewState::showItems,viewState::handleError)
        }
    }

    fun onItemClick(card: AbstractCard) {
        viewState.showDetails(card)
    }

    fun loadCardsByQuery(query: String, userId: String) {
        RepositoryProvider.abstractCardRepository!!
                .findDefaultAbstractCardsByQuery(query, userId)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe({ viewState.showItems(it) }, { viewState.handleError(it) })
    }

    fun loadUserCardsByQuery(query: String, userId: String) {
        RepositoryProvider.abstractCardRepository!!
                .findMyAbstractCardsByQuery(query, userId)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe({ viewState.showItems(it) }, { viewState.handleError(it) })
    }
}