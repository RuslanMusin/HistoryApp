package com.summer.itis.summerproject.ui.tests.one_test_list

import android.widget.ProgressBar
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.summer.itis.summerproject.model.Test
import com.summer.itis.summerproject.ui.base.BaseAdapter
import com.summer.itis.summerproject.ui.tests.test_list.TestAdapter
import io.reactivex.disposables.Disposable

interface OneTestListView : MvpView, BaseAdapter.OnItemClickListener<Test> {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun handleError(throwable: Throwable)

    fun setNotLoading()

    fun showLoading(disposable: Disposable)

    fun hideLoading()

    fun loadNextElements(i: Int)

    fun changeDataSet(friends: List<Test>)
}
