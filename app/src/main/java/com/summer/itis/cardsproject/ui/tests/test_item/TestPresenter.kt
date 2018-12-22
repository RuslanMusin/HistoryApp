package com.summer.itis.summerproject.ui.tests.test_item

import android.util.Log

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.summer.itis.summerproject.ui.tests.add_test.AddTestView

import com.summer.itis.summerproject.utils.Const.TAG_LOG

@InjectViewState
class TestPresenter : MvpPresenter<TestView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d(TAG_LOG, "attach listPresenter")
    }
}
