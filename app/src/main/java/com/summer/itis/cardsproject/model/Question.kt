package com.summer.itis.cardsproject.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.Expose

@IgnoreExtraProperties
class Question {

    var id: String? = null

    var question: String? = null

    var answers: MutableList<Answer> = ArrayList()

    @field:Exclude var userRight: Boolean = false
}
