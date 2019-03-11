package com.summer.itis.cardsproject.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class UserEpoch {

    lateinit var id: String
    lateinit var userId: String
    lateinit var epochId: String
    var win: Int = 0
    var lose: Int = 0
    var sum: Int = win + lose
         get() = win + lose
    @Exclude
    @Transient
    var epoch: Epoch? = null

    var right: Int = 0
    var wrong: Int = 0

    var ge: Double = 0.0
    var lastGe: Double = 0.0
    var geSub: Double = ge - lastGe
        get() = ge - lastGe
    var ke: Double = 0.0
    var lastKe: Double = 0.0
    var keSub: Double = ke - lastKe
        get() = ke - lastKe
    var updateDate: Long = 0

    constructor() {}

    constructor(epoch: Epoch, user: User) {
        this.id = epoch.id
        this.epoch = epoch
        this.userId = user.id
        this.epochId = epoch.id
        this.ge = 0.0
        this.updateDate = Date().time
    }

    fun updateGe() {
        ge = ((win - lose) / sum).toDouble()
    }
}
