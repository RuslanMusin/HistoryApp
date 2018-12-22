package com.summer.itis.cardsproject.model.game

import com.summer.itis.cardsproject.model.Card

class CardChoose {

    lateinit var cardId: String
    lateinit var questionId: String
    var card: Card? = null

    constructor() {

    }

    constructor(cardId: String, questionId: String) {
        this.cardId = cardId
        this.questionId = questionId
    }

    constructor(card: Card, questionId: String) {
        this.card = card
        this.questionId = questionId
    }

    companion object {
        val PARAM_cardId = "cardId"
        val PARAM_questionId = "questionId"
    }
}
