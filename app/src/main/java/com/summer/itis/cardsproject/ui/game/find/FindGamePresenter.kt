package com.summer.itis.cardsproject.ui.game.find

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.summer.itis.cardsproject.model.Card
import com.summer.itis.cardsproject.model.game.Lobby
import com.summer.itis.cardsproject.model.game.LobbyPlayerData
import com.summer.itis.cardsproject.repository.RepositoryProvider
import com.summer.itis.cardsproject.repository.json.GamesRepository
import com.summer.itis.cardsproject.repository.json.UserRepository
import com.summer.itis.cardsproject.ui.service.GameService
import com.summer.itis.cardsproject.utils.Const.BOT_GAME
import com.summer.itis.cardsproject.utils.Const.ONLINE_GAME

@InjectViewState
class FindGamePresenter() : MvpPresenter<FindGameView>() {

    init {
        //TODO resetableLazy?
        RepositoryProvider.gamesRepository.resetData()

        viewState.showNothing()
        RepositoryProvider.cardRepository.findMyCards(UserRepository.currentId)
                .subscribe { t: List<Card>? ->
                    //                    Log.d("Alm","FindGamePresenter subscribe")
                    if (t!!.size >= GamesRepository.ROUNDS_COUNT) {
                        viewState.showNotSearching()
                    } else {
                        viewState.showNotEnoughCards()
                    }
                }
    }

    val gamesRepository = RepositoryProvider.gamesRepository

    fun findGame() {
        viewState.showSearching()
//        gamesRepository.startSearchGame { viewState.gameFinded(ONLINE_GAME) }.subscribe()
    }

   /* fun findBotGame() {
        viewState.showSearching()
        val lobby: Lobby = Lobby()
        lobby.id = UserRepository.currentId
        lobby.creator = LobbyPlayerData(UserRepository.currentId,true,null,null,null)
        lobby.status = GAME_STARTED_STATUS
        gamesRepository.createLobby(lobby) {

            gamesRepository.joinBot().subscribe{ e ->
                viewState.gameFinded(BOT_GAME)
            }
        }

    }*/


    fun cancelSearching() {
        gamesRepository.cancelSearchGame { viewState.showNotSearching() }
    }
}
