package com.summer.itis.cardsproject.ui.member.member_list.reader

import android.widget.ProgressBar

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.summer.itis.cardsproject.model.User
import com.summer.itis.cardsproject.ui.base.BaseAdapter
import com.summer.itis.cardsproject.ui.member.member_list.MemberAdapter

import io.reactivex.disposables.Disposable

@StateStrategyType(AddToEndSingleStrategy::class)
interface ReaderListView : MvpView, BaseAdapter.OnItemClickListener<User> {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun handleError(throwable: Throwable)

    fun setNotLoading()

    fun showLoading(disposable: Disposable)

    fun hideLoading()

    fun showDetails(comics: User)

    fun loadNextElements(i: Int)

    fun setCurrentType(type: String)

    fun setAdapter(adapter: MemberAdapter)

    fun loadRequests(currentId: String)

    fun loadFriends(currentId: String)

    fun loadReaders()

    fun setProgressBar(progressBar: ProgressBar?)

    fun changeAdapter(position: Int)

    fun changeDataSet(friends: MutableList<User>)
}
