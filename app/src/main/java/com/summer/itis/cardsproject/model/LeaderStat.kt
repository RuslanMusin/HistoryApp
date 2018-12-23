package com.summer.itis.cardsproject.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class LeaderStat {

    lateinit var id: String
    lateinit var name: String
    var level: Int = 0
    var kg: Double = 0.0
    var win: Int = 0
    var lose: Int = 0

    constructor() {}

    constructor(user: User) {
        this.id = user.id
        this.name = user.username!!
        this.level = user.level
        var ge = 0.0
        val list = user.epochList
        for(item in list) {
            ge += item.ge
            this.win += item.win
            this.lose += item.lose
        }
        this.kg = ge / list.size
    }


}