package com.summer.itis.summerproject.ui.game.add_photo

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.summer.itis.summerproject.model.Question
import com.summer.itis.summerproject.model.db_dop_models.PhotoItem
import io.reactivex.disposables.Disposable

interface AddPhotoView : MvpView {

    fun handleError(throwable: Throwable)

    fun showLoading(disposable: Disposable)

    fun hideLoading()

    fun changeDataSet(friends: List<PhotoItem>)
}