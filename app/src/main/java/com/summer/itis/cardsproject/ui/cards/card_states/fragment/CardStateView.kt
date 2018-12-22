package com.summer.itis.cardsproject.ui.cards.card_states.fragment

import com.arellomobile.mvp.MvpView
import com.summer.itis.cardsproject.model.Test

interface CardStateView : MvpView {

    fun setTestData(test: Test)

}