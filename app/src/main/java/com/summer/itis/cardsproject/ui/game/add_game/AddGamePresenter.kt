package com.summer.itis.cardsproject.ui.game.add_game

import android.app.Notification
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.summer.itis.cardsproject.model.game.Lobby
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.gamesRepository
import com.summer.itis.cardsproject.ui.tests.add_test.AddTestView
import com.summer.itis.cardsproject.utils.AppHelper
import com.summer.itis.cardsproject.utils.Const
import com.summer.itis.cardsproject.utils.Const.TAG_LOG

@InjectViewState
class AddGamePresenter : MvpPresenter<AddGameView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d(TAG_LOG, "attach listPresenter")
    }

    fun createGame(lobby: Lobby) {
        lobby.usersIds.add(AppHelper.currentUser.id)
        gamesRepository.createLobby(lobby) {

            viewState.onGameCreated() }
    }
}