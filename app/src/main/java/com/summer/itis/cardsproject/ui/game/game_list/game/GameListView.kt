package com.summer.itis.cardsproject.ui.game.game_list.game

import android.widget.ProgressBar
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.summer.itis.cardsproject.model.Test
import com.summer.itis.cardsproject.model.game.Lobby
import com.summer.itis.cardsproject.model.game.LobbyPlayerData
import com.summer.itis.cardsproject.ui.base.BaseAdapter
import com.summer.itis.cardsproject.ui.game.game_list.GameAdapter
import com.summer.itis.cardsproject.ui.tests.test_list.TestAdapter
import io.reactivex.disposables.Disposable

interface GameListView : MvpView, BaseAdapter.OnItemClickListener<Lobby> {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun handleError(throwable: Throwable)

    fun setNotLoading()

    fun showLoading(disposable: Disposable)

    fun hideLoading()

    fun showDetails(comics: Lobby)

    fun loadNextElements(i: Int)

    fun setCurrentType(type: String)

    fun setAdapter(adapter: GameAdapter)

    fun loadOfficialTests()

    fun loadUserTests()

    fun setProgressBar(progressBar: ProgressBar?)

    fun changeAdapter(position: Int)

    fun changeDataSet(friends: List<Lobby>)

    fun onGameFinded()

    fun onBotGameFinded()

    fun waitEnemy()

    fun showSnackbar(msg: String)

    fun showProgressDialog()

    fun hideProgressDialog()
}
