package com.summer.itis.summerproject.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.summer.itis.summerproject.model.game.Lobby
import com.summer.itis.summerproject.utils.Const.OFFLINE_STATUS

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

    constructor() {}

    constructor(email: String, username: String) {
        this.email = email
        this.username = username
    }
}
