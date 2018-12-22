package com.summer.itis.cardsproject.ui.tests.test_list.test

import android.widget.ProgressBar

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.summer.itis.cardsproject.model.Test
import com.summer.itis.cardsproject.ui.base.BaseAdapter
import com.summer.itis.cardsproject.ui.tests.test_list.TestAdapter

import io.reactivex.disposables.Disposable

interface TestListView : MvpView, BaseAdapter.OnItemClickListener<Test> {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun handleError(throwable: Throwable)

    fun setNotLoading()

    fun showLoading(disposable: Disposable)

    fun hideLoading()

    fun showDetails(comics: Test)

    fun loadNextElements(i: Int)

    fun setCurrentType(type: String)

    fun setAdapter(adapter: TestAdapter)

    fun loadOfficialTests()

    fun loadUserTests()

    fun loadMyTests(userId:String)

    fun setProgressBar(progressBar: ProgressBar?)

    fun changeAdapter(position: Int)

    fun changeDataSet(friends: List<Test>)
}
