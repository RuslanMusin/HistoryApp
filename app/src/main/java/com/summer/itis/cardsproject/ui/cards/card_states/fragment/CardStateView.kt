package com.summer.itis.summerproject.ui.cards.card_states.fragment

import com.arellomobile.mvp.MvpView
import com.summer.itis.summerproject.model.Test

interface CardStateView : MvpView {

    fun setTestData(test: Test)

}