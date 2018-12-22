package com.summer.itis.summerproject.ui.game.add_game

import android.app.Notification
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.summer.itis.summerproject.model.game.Lobby
import com.summer.itis.summerproject.repository.RepositoryProvider.Companion.gamesRepository
import com.summer.itis.summerproject.ui.tests.add_test.AddTestView
import com.summer.itis.summerproject.utils.Const
import com.summer.itis.summerproject.utils.Const.TAG_LOG

@InjectViewState
class AddGamePresenter : MvpPresenter<AddGameView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d(TAG_LOG, "attach listPresenter")
    }

    fun createGame(lobby: Lobby) {
        gamesRepository.createLobby(lobby) {

            viewState.onGameCreated() }
    }
}