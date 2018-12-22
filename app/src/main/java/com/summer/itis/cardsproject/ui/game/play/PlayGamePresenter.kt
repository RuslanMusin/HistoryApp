package com.summer.itis.cardsproject.ui.game.play

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.summer.itis.cardsproject.model.Card
import com.summer.itis.cardsproject.model.User
import com.summer.itis.cardsproject.model.game.CardChoose
import com.summer.itis.cardsproject.model.game.Lobby
import com.summer.itis.cardsproject.repository.RepositoryProvider
import com.summer.itis.cardsproject.repository.json.GamesRepository
import com.summer.itis.cardsproject.repository.json.UserRepository
import com.summer.itis.cardsproject.utils.Const.BOT_GAME
import com.summer.itis.cardsproject.utils.Const.BOT_ID
import com.summer.itis.cardsproject.utils.Const.MODE_PLAY_GAME
import com.summer.itis.cardsproject.utils.Const.OFFICIAL_TYPE
import com.summer.itis.cardsproject.utils.Const.TAG_LOG
import com.summer.itis.cardsproject.utils.getRandom
import io.reactivex.Single
import java.util.*

@InjectViewState
class PlayGamePresenter() : MvpPresenter<PlayGameView>(), GamesRepository.InGameCallbacks {


    val gamesRepository = RepositoryProvider.gamesRepository
    val cardsRepository = RepositoryProvider.cardRepository

    lateinit var botCards: MutableList<Card>

    lateinit var lobby: Lobby

    fun setInitState(initlobby: Lobby) {
        lobby = initlobby
        gamesRepository.setLobbyRefs(lobby.id)
        gamesRepository.watchMyStatus()
        val single: Single<List<Card>>
        if(lobby.type.equals(OFFICIAL_TYPE)) {
            single = cardsRepository.findOfficialMyCards(UserRepository.currentId)
        } else {
            single = cardsRepository.findMyCards(UserRepository.currentId)
        }
        single.subscribe { cards: List<Card>? ->
            cards?.let {
                val mutCards = cards.toMutableList()
                val myCards: MutableList<Card> = ArrayList()

                for (i in 1..lobby.cardNumber) {
                    mutCards.getRandom()?.let {
                        Log.d(TAG_LOG,"random card num = $i and name = ${it.abstractCard?.name}")
                        myCards.add(it)
                        mutCards.remove(it)
                    }
                }
                if (cards.size > lobby.cardNumber) {
                    viewState.changeCards(myCards,mutCards)
                } else {
                    changeGameMode(MODE_PLAY_GAME)
                    setCardList(myCards, 20000)
                }
            }
        }
    }

    fun waitEnemyGameMode(mode: String): Single<Boolean> {
        Log.d(TAG_LOG,"wait mode  = $mode")
        return gamesRepository.waitGameMode(mode)
    }

    fun changeGameMode(mode: String) {
        Log.d(TAG_LOG,"change mode = $mode")
        gamesRepository.changeGameMode(mode).subscribe()
    }

    fun setCardList(myCards: List<Card>, time: Long) {
        Log.d(TAG_LOG,"set card list")
        viewState.waitEnemyTimer(time)
        waitEnemyGameMode(MODE_PLAY_GAME).subscribe { e ->
            viewState.setCardsList(ArrayList(myCards))
            viewState.setCardChooseEnabled(true)

            /* RepositoryProvider.userRepository.readUserById(gamesRepository.enemyId!!)
                .subscribe { t: User? ->
                    viewState.setEnemyUserData(t!!)
                }*/
            lobby.gameData?.enemyId?.let {
                RepositoryProvider.userRepository.readUserById(it)
                        .subscribe { t: User? ->
                            viewState.setEnemyUserData(t!!)
                        }
            }

            gamesRepository.startGame(lobby, this)

            if (lobby.gameData?.gameMode.equals(BOT_GAME)) {
                Log.d(TAG_LOG, "find bot cards")
                val single: Single<List<Card>>
                if (lobby.type.equals(OFFICIAL_TYPE)) {
                    single = cardsRepository.findOfficialMyCards(BOT_ID)
                } else {
                    single = cardsRepository.findMyCards(BOT_ID)
                }
                single.subscribe { cards ->
                    gamesRepository.selectOnBotLoseCard(cards)
                    botCards = cards.toMutableList()
                }
            }
        }
    }

    fun chooseCard(card: Card) {
        gamesRepository.findLobby(lobby.id).subscribe { e ->
            viewState.setCardChooseEnabled(false)
            gamesRepository.chooseNextCard(lobby, card.id!!)
            viewState.showYouCardChoose(card)
            youCardChosed = true
            if (lobby.gameData?.gameMode.equals(BOT_GAME)) {
                Log.d(TAG_LOG, "bot choose card")
                botChooseCard()
            }
        }

            /* if (enemyCardChosed) {
                 showQuestion()
             }*/
    }

    fun botChooseCard() {
        val card: Card? = botCards.getRandom()
        botCards.remove(card)
        card?.let {
//            viewState.showEnemyCardChoose(it)
            card.id?.let { it1 -> gamesRepository.botNextCard(lobby, it1) }
        }
//        enemyCardChosed = true
//        showQuestion()

    }

    fun answer(correct: Boolean) {
        viewState.hideQuestionForYou()

        viewState.hideEnemyCardChoose()
        viewState.hideYouCardChoose()

        viewState.showYourAnswer(correct)

        gamesRepository.findLobby(lobby.id).subscribe { e ->
            gamesRepository.answerOnLastQuestion(lobby, correct)
            enemyCardChosed = false
            youCardChosed = false

            if (lobby.gameData?.gameMode.equals(BOT_GAME)) {
                Log.d(TAG_LOG, "bot answer")
                answerBot()
            }
        }


    }

    fun answerBot() {
        val correct: Boolean = Random().nextBoolean()
        gamesRepository.botAnswer(lobby,correct)
    }

    override fun onGameEnd(type: GamesRepository.GameEndType, card: Card) {
        Log.d("Alm", "Game End: " + type)

        viewState.showGameEnd(type,card)

//        when(type){
//            GamesRepository.GameEndType.YOU_WIN->{
//                viewState.
//            }
//        }

    }

    var youCardChosed = false
    var enemyCardChosed = false

    var lastEnemyChoose: CardChoose? = null

    override fun onEnemyCardChosen(choose: CardChoose) {
        Log.d("Alm", "enemy chosen card " + choose.cardId)
        Log.d("Alm", "enemy chosen question " + choose.questionId)
        enemyCardChosed = true
        lastEnemyChoose = choose
        RepositoryProvider.cardRepository.readCard(choose.cardId).subscribe { card ->
            viewState.showEnemyCardChoose(card)
        }
//        viewState.setCardChooseEnabled(true)
    }

    fun enemyDisconnected() {
        gamesRepository.onEnemyDisconnectAndYouWin(lobby)
    }

    fun showQuestion() {
        gamesRepository.findLobby(lobby.id).subscribe { e ->
            if (youCardChosed and enemyCardChosed) {
                RepositoryProvider.cardRepository.readCard(lastEnemyChoose!!.cardId).subscribe { card ->
                    viewState.showQuestionForYou(card.test.questions
                            .first { q -> q.id == lastEnemyChoose!!.questionId })
                }
            }
        }

    }

    override fun onEnemyAnswered(correct: Boolean) {
        viewState.showEnemyAnswer(correct)
        viewState.setCardChooseEnabled(true)
    }
}
