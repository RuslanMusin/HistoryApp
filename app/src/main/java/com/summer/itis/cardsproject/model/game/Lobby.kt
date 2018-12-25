package com.summer.itis.cardsproject.model.game

import com.google.firebase.database.Exclude
import com.summer.itis.cardsproject.model.Epoch
import com.summer.itis.cardsproject.model.User
import com.summer.itis.cardsproject.utils.Const.CARD_NUMBER
import com.summer.itis.cardsproject.utils.Const.OFFICIAL_TYPE
import com.summer.itis.cardsproject.utils.Const.ONLINE_GAME
import com.summer.itis.cardsproject.utils.Const.ONLINE_STATUS
import com.summer.itis.cardsproject.utils.Const.USER_TYPE

class Lobby {

    lateinit var id: String
    var title: String? = null
    var lowerTitle: String? = null
    lateinit var epochId: String
    lateinit var epoch: Epoch
    var photoUrl: String? = null
    var cardNumber: Int = CARD_NUMBER
    var status: String = ONLINE_STATUS
    var type: String = OFFICIAL_TYPE
    var isFastGame: Boolean = false

    @Exclude
    var isMyCreation: Boolean = false

    var creator: LobbyPlayerData? = null
    var invited: LobbyPlayerData? = null

    @Exclude
    var gameData: GameData? = null

    @Exclude
    var lobbyData: LobbyData? = null

    var usersIds: MutableList<String> = ArrayList()

    @Exclude
    var userList: MutableList<User> = ArrayList()

    companion object {
        val PARAM_creator = "creator"
        val PARAM_invited = "invited"

        const val ONLINE_GAME = "online_game"
    }
}
