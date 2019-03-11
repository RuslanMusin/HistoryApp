package com.summer.itis.cardsproject.model.game

import com.google.firebase.database.Exclude
import com.summer.itis.cardsproject.model.Epoch
import com.summer.itis.cardsproject.model.User

class LobbyData {

    lateinit var id: String

    var cardNumber: Int = 5

    lateinit var epochId: String

    @Exclude
    lateinit var epoch: Epoch

    var usersIds: MutableList<String> = ArrayList()

    @Exclude
    var userList: MutableList<User> = ArrayList()
    @Exclude
    var likes: Double = 0.0
}