package com.summer.itis.summerproject.ui.cards.add_card

import com.arellomobile.mvp.MvpView
import com.summer.itis.summerproject.model.Test
import com.summer.itis.summerproject.model.pojo.opensearch.Item
import com.summer.itis.summerproject.model.pojo.query.Page

interface AddCardView : MvpView {

    fun setQueryResults(list: List<Page>)

    fun handleError(throwable: Throwable)
}

