package com.summer.itis.summerproject.ui.tests.add_test

import com.arellomobile.mvp.MvpView
import com.summer.itis.summerproject.model.Question
import com.summer.itis.summerproject.model.Test

interface AddTestView : MvpView {

    fun setQuestion(question: Question)

    fun createTest()

    fun setTest(test: Test)
}
