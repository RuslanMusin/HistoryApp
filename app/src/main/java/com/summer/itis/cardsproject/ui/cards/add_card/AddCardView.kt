package com.summer.itis.cardsproject.ui.cards.add_card

import com.arellomobile.mvp.MvpView
import com.summer.itis.cardsproject.model.Test
import com.summer.itis.cardsproject.model.pojo.opensearch.Item
import com.summer.itis.cardsproject.model.pojo.query.Page

interface AddCardView : MvpView {

    fun setQueryResults(list: List<Page>)

    fun handleError(throwable: Throwable)
}

