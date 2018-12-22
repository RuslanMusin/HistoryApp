package com.summer.itis.summerproject.ui.tests.add_test

import android.util.Log

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.summer.itis.summerproject.utils.Const.TAG_LOG

@InjectViewState
class AddTestPresenter : MvpPresenter<AddTestView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d(TAG_LOG, "attach listPresenter")
    }
}
