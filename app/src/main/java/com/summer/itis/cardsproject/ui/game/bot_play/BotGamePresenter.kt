package com.summer.itis.summerproject.ui.game.bot_play

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.summer.itis.summerproject.R.string.card
import com.summer.itis.summerproject.model.Card
import com.summer.itis.summerproject.model.User
import com.summer.itis.summerproject.model.game.CardChoose
import com.summer.itis.summerproject.model.game.GameData
import com.summer.itis.summerproject.model.game.Lobby
import com.summer.itis.summerproject.model.game.LobbyPlayerData
import com.summer.itis.summerproject.repository.RepositoryProvider
import com.summer.itis.summerproject.repository.RepositoryProvider.Companion.gamesRepository
import com.summer.itis.summerproject.repository.RepositoryProvider.Companion.userRepository
import com.summer.itis.summerproject.repository.json.GamesRepository
import com.summer.itis.summerproject.repository.json.GamesRepository.Companion.FIELD_ONLINE
import com.summer.itis.summerproject.repository.json.UserRepository
import com.summer.itis.summerproject.ui.game.play.PlayGameView
import com.summer.itis.summerproject.utils.Const
import com.summer.itis.summerproject.utils.Const.OFFLINE_STATUS
import com.summer.itis.summerproject.utils.Const.TAG_LOG
import com.summer.itis.summerproject.utils.getRandom
import io.reactivex.Single
import java.util.*

@InjectViewState
class BotGamePresenter() : MvpPresenter<BotGameView>(), GamesRepository.InGameCallbacks {


    val gamesRepository = RepositoryProvider.gamesRepository
    val cardsRepository = RepositoryProvider.cardRepository

    lateinit var botCards: MutableList<Card>
    lateinit var myCards: MutableList<Card>

    lateinit var lobby: Lobby

    fun setInitState(initlobby: Lobby) {
        lobby = initlobby
        gamesRepository.setLobbyRefs(lobby.id)
        val single: Single<List<Card>>
        if(lobby.type.equals(Const.OFFICIAL_TYPE)) {
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
                        Log.d(Const.TAG_LOG,"random card num = $i and name = ${it.abstractCard?.name}")
                        myCards.add(it)
                        mutCards.remove(it)
                    }
                }
                if (cards.size > lobby.cardNumber) {
                    viewState.changeCards(myCards,mutCards)
                } else {
//                    changeGameMode(Const.MODE_PLAY_GAME)
                    setCardList(myCards)
                }
            }
        }
    }

    fun setCardList(myCards: List<Card>) {
        Log.d(Const.TAG_LOG, "set card list")
//        waitEnemyGameMode(Const.MODE_PLAY_GAME).subscribe { e ->
        this.myCards = myCards.toMutableList()
        viewState.setCardsList(ArrayList(myCards))
        viewState.setCardChooseEnabled(true)

        lobby.gameData?.enemyId?.let {
            RepositoryProvider.userRepository.readUserById(it)
                    .subscribe { t: User? ->
                        viewState.setEnemyUserData(t!!)
                    }
        }

        if (lobby.gameData?.gameMode.equals(Const.BOT_GAME)) {
            Log.d(Const.TAG_LOG, "find bot cards")
            val single: Single<List<Card>>
            if (lobby.type.equals(Const.OFFICIAL_TYPE)) {
                single = cardsRepository.findOfficialMyCards(Const.BOT_ID)
            } else {
                single = cardsRepository.findMyCards(Const.BOT_ID)
            }
            single.subscribe { cards ->
//                gamesRepository.selectOnBotLoseCard(cards)
                botCards = cards.toMutableList()
                startGame()

            }
        }

//            gamesRepository.startGame(lobby, this)


//        }
    }

    fun chooseCard(card: Card) {
//        gamesRepository.findLobby(lobby.id).subscribe { e ->
        viewState.setCardChooseEnabled(false)
//            gamesRepository.chooseNextCard(lobby, card.id!!)
        viewState.showYouCardChoose(card)
        youCardChosed = true
        val questionId = card.test.questions.getRandom()!!.id
        val choose = CardChoose(card, questionId!!)
        lobby.gameData?.lastMyChosenCard = choose
        if (lobby.gameData?.gameMode.equals(Const.BOT_GAME)) {
            Log.d(Const.TAG_LOG, "bot choose card")
            botChooseCard()
        }
//        }

        /* if (enemyCardChosed) {
             showQuestion()
         }*/
    }

    fun botChooseCard() {
        val card: Card? = botCards.getRandom()
        botCards.remove(card)
        card?.let {
            //            viewState.showEnemyCardChoose(it)
//            card.id?.let { it1 -> gamesRepository.botNextCard(lobby, it1)
            val questionId = card.test.questions.getRandom()!!.id
            val choose = CardChoose(card, questionId!!)
            enemyCardChosed = true
            lastEnemyChoose = choose
            lobby.gameData?.lastEnemyChoose = lastEnemyChoose
            viewState.showEnemyCardChoose(card)
        }


        /* RepositoryProvider.cardRepository.readCard(choose.cardId).subscribe { card ->
            viewState.showEnemyCardChoose(card)
        }*/
//        enemyCardChosed = true
//        showQuestion()

    }

    fun answer(correct: Boolean) {
        viewState.hideQuestionForYou()

        viewState.hideEnemyCardChoose()
        viewState.hideYouCardChoose()

        viewState.showYourAnswer(correct)

//        gamesRepository.findLobby(lobby.id).subscribe { e ->
//            gamesRepository.answerOnLastQuestion(lobby, correct)

            lobby.gameData?.let{
                it.my_answers++
                if(correct) {
                    it.my_score++
                }
            }


            enemyCardChosed = false
            youCardChosed = false

            if (lobby.gameData?.gameMode.equals(Const.BOT_GAME)) {
                Log.d(Const.TAG_LOG, "bot answer")
                answerBot()
            }

            checkGameEnd(lobby)
//        }


    }

    fun answerBot() {
        val correct: Boolean = Random().nextBoolean()
        lobby.gameData?.let{
            it.enemy_answers++
            if(correct) {
                it.enemy_score++
            }
        }
        viewState.showEnemyAnswer(correct)
        viewState.setCardChooseEnabled(true)
//        gamesRepository.botAnswer(lobby,correct)
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

    fun showQuestion() {
        /*  gamesRepository.findLobby(lobby.id).subscribe { e ->
            if (youCardChosed and enemyCardChosed) {
                RepositoryProvider.cardRepository.readCard(lastEnemyChoose!!.cardId).subscribe { card ->
                    viewState.showQuestionForYou(card.test.questions
                            .first { q -> q.id == lastEnemyChoose!!.questionId })
                }
            }
        }*/
        if (youCardChosed and enemyCardChosed) {
            lastEnemyChoose?.card?.let {
                viewState.showQuestionForYou(it.test.questions
                        .first { q -> q.id == lastEnemyChoose!!.questionId })
            }

        }
    }

    override fun onEnemyAnswered(correct: Boolean) {
        viewState.showEnemyAnswer(correct)
        viewState.setCardChooseEnabled(true)
    }

    private fun checkGameEnd(lobby: Lobby) {
        if (lobby.gameData?.enemy_answers == GamesRepository.ROUNDS_COUNT && lobby.gameData?.my_answers == GamesRepository.ROUNDS_COUNT) {
            Log.d("Alm", "repo: GAME END!!!")


            //TODO
            val myScore = lobby.gameData?.my_score
            val enemyScore = lobby.gameData?.enemy_score

            if(myScore != null && enemyScore != null) {
                if (myScore > enemyScore) {
                    onWin(lobby)

                } else if (enemyScore > myScore) {
                    onLose()

                } else {
                    //TODO
                    compareLastCards(lobby)
                }
            }


        }
    }

    private fun compareLastCards(lobby: Lobby) {
        val myLastCard: Card? = lobby.gameData?.lastMyChosenCard?.card
        val enemyLastCard: Card? = lobby.gameData?.lastEnemyChoose?.card

        var c = 0

        c += compareCardsParameter({ card -> card.intelligence!! }, myLastCard!!, enemyLastCard!!)
        c += compareCardsParameter({ card -> card.support!! }, myLastCard!!, enemyLastCard!!)
        c += compareCardsParameter({ card -> card.prestige!! }, myLastCard!!, enemyLastCard!!)
        c += compareCardsParameter({ card -> card.hp!! }, myLastCard!!, enemyLastCard!!)
        c += compareCardsParameter({ card -> card.strength!! }, myLastCard!!, enemyLastCard!!)

        if (c > 0) {
            onWin(lobby)
        } else if (c < 0) {
            onLose()
        } else {
            onDraw()
        }


    }

    private fun onDraw() {
        lobby.gameData?.onYouLoseCard?.let { onGameEnd(GamesRepository.GameEndType.DRAW, it) }
    }

    fun compareCardsParameter(f: ((card: Card) -> Int), card1: Card, card2: Card): Int {
        return f(card1).compareTo(f(card2))
    }

    private fun onWin(lobby: Lobby) {
        //TODO move card

        lobby.gameData?.onEnemyLoseCard?.let { onGameEnd(GamesRepository.GameEndType.YOU_WIN, it) }


    }

    private fun onLose() {
        lobby.gameData?.onYouLoseCard?.let { onGameEnd(GamesRepository.GameEndType.YOU_LOSE, it)}
    }

    fun onDisconnectAndLose() {
        lobby.gameData?.onYouLoseCard?.let { onGameEnd(GamesRepository.GameEndType.YOU_DISCONNECTED_AND_LOSE, it) }
    }

    fun startGame() {

        var onYouLoseCard = ArrayList(myCards).minus(botCards).getRandom()
        if(onYouLoseCard == null) {
            onYouLoseCard = myCards.getRandom()
        }
        lobby.gameData?.onYouLoseCard = onYouLoseCard

        lobby.gameData?.onEnemyLoseCard = botCards.getRandom()


        userRepository.checkUserConnection {
            Log.d(TAG_LOG,"disconnect bot and me")
            onDisconnectAndLose()
        }
    }
}
