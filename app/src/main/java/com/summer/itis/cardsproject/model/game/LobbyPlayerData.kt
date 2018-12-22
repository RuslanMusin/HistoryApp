package com.summer.itis.summerproject.model.game

import com.summer.itis.summerproject.utils.Const.MODE_CHANGE_CARDS

class LobbyPlayerData() {

    lateinit var playerId: String
    var online: Boolean = false
    var randomSendOnLoseCard: String? = null
    var choosedCards: Map<String, CardChoose>? = null
    var answers: Map<String, Boolean>? = null
    var mode: String = MODE_CHANGE_CARDS

    companion object {
        val PARAM_playerId = "playerId"
        val PARAM_online = "online"
        val PARAM_randomSendOnLoseCard = "randomSendOnLoseCard"
        val PARAM_choosedCards = "choosedCards"
        val PARAM_answers = "answers"
    }
}
