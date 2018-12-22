package com.summer.itis.cardsproject.model.game

import com.summer.itis.cardsproject.model.Card
import com.summer.itis.cardsproject.repository.json.GamesRepository.Companion.FIELD_CREATOR
import com.summer.itis.cardsproject.utils.Const.BOT_GAME

class GameData {

    var lastEnemyChoose: CardChoose? = null
    var lastMyChosenCard: CardChoose? = null

    lateinit var enemyId: String
    var gameMode: String = BOT_GAME
    var role: String = FIELD_CREATOR

    var enemy_answers = 0;
    var enemy_score = 0;

    var my_answers = 0;
    var my_score = 0;

    var onYouLoseCard: Card? = null
    var onEnemyLoseCard: Card? = null
}