package com.summer.itis.cardsproject.ui.tests.add_test

import com.arellomobile.mvp.MvpView
import com.summer.itis.cardsproject.model.Question
import com.summer.itis.cardsproject.model.Test

interface AddTestView : MvpView {

    fun setQuestion(question: Question)

    fun createTest()

    fun setTest(test: Test)
}
