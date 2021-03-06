package com.summer.itis.cardsproject.ui.game.bot_play

import com.arellomobile.mvp.MvpView
import com.summer.itis.cardsproject.model.Card
import com.summer.itis.cardsproject.model.Question
import com.summer.itis.cardsproject.model.User
import com.summer.itis.cardsproject.repository.json.GamesRepository
import com.summer.itis.cardsproject.ui.game.play.PlayView

interface BotGameView : PlayView {
    fun setEnemyUserData(user: User)

    fun setCardsList(cards: ArrayList<Card>)

    fun changeCards(cards: MutableList<Card>, mutCards: MutableList<Card>)

    fun setCardChooseEnabled(enabled: Boolean)

    fun showEnemyCardChoose(card: Card)
    fun hideEnemyCardChoose()

    fun showQuestionForYou(question: Question)
    fun hideQuestionForYou()

    fun showYouCardChoose(choose: Card)//CardChooose? чтобы видеть вопрос для противника
    fun hideYouCardChoose()

    fun showEnemyAnswer(correct: Boolean)
    fun showYourAnswer(correct: Boolean)

    fun showGameEnd(type: GamesRepository.GameEndType, card: Card)
}