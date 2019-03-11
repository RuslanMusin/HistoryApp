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

    lateinit var epochId: String

    @Exclude
    var comments: MutableList<Comment> = ArrayList()

    @Exclude
    var card: Card? = null

    @Exclude
    var epoch: Epoch? = null

    @Exclude
    var testDone: Boolean = false

    @field:Exclude var testRelation: Relation? = null

    @Exclude
    lateinit var rightQuestions: List<Question>

    @Exclude
    lateinit var wrongQuestions: List<Question>

    var usersIds: MutableList<String> = ArrayList()
    var usersList: MutableList<User> = ArrayList()

    var likes = 0.0

    override fun equals(other: Any?): Boolean {
        var flag = false
        val test = other as Test
        if(id?.equals(test.id)!!) {
            flag = true
        }
        return flag
    }

    override fun hashCode(): Int {
        var arr = id?.toCharArray()
        var hash = 31
        for(a in arr!!) {
            hash += a.toInt()
        }
        return hash
    }
}
