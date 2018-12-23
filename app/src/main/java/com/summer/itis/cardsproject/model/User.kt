package com.summer.itis.cardsproject.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.summer.itis.cardsproject.model.game.Lobby
import com.summer.itis.cardsproject.utils.Const.OFFLINE_STATUS

@IgnoreExtraProperties
class User {

    lateinit var id: String

    var email: String? = null

    var username: String? = null

    var lowerUsername: String? = null

    var photoUrl: String? = null

    var isStandartPhoto: Boolean = true

    var desc: String? = null

    var score: String? = null

    var role: String? = null

    var status: String = OFFLINE_STATUS

    var lobbyId: String? = null

    @Exclude
    var gameLobby: Lobby? = null

    @Exclude
    private val cards: List<Card>? = null

    @Exclude
    private val tests: List<Test>? = null

    var epochList: MutableList<UserEpoch> = ArrayList()

    var level: Int = 1

    var points: Long = 0

    var nextLevel: Long = 60

    constructor() {}

    constructor(email: String, username: String) {
        this.email = email
        this.username = username
    }
}
