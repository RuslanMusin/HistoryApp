package com.summer.itis.cardsproject.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.Expose
import com.summer.itis.cardsproject.model.db_dop_models.Relation

import java.util.ArrayList

@IgnoreExtraProperties
class Test {

    var id: String? = null

    var title: String? = null

    var lowerTitle: String? = null

    var desc: String? = null

    var authorId: String? = null

    var authorName: String? = null

    var cardId: String? = null

    var questions: MutableList<Question> = ArrayList()

    var type: String? = null

    var imageUrl: String? = null

    @Exclude
    var comments: MutableList<Comment> = ArrayList()

    @Exclude
    var card: Card? = null

    @Exclude
    var testDone: Boolean = false

    @field:Exclude var testRelation: Relation? = null

    @Exclude
    lateinit var rightQuestions: List<Question>

    @Exclude
    lateinit var wrongQuestions: List<Question>
}
