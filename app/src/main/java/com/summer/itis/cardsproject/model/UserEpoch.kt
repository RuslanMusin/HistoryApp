package com.summer.itis.cardsproject.model

import com.google.firebase.database.Exclude
import java.util.*

class UserEpoch {

    lateinit var id: String
    lateinit var userId: String
    lateinit var epochId: String
    var win: Int = 0
    var lose: Int = 0
    var sum: Int = win + lose

    @Exclude
    lateinit var epoch: Epoch

    var right: Int = 0
    var wrong: Int = 0

    var ge: Double = ((win - lose) / sum).toDouble()
    var lastGe: Double = 0.0
    var geSub: Double = ge - lastGe
    var ke: Double = 0.0
    var lastKe: Double = 0.0
    var keSub: Double = ke - lastKe

    var updateDate: Long = 0

    constructor() {}

    constructor(epoch: Epoch, user: User) {
        this.epoch = epoch
        this.userId = user.id
        this.epochId = epoch.id
        this.ge = 0.0
        this.updateDate = Date().time
    }
}
