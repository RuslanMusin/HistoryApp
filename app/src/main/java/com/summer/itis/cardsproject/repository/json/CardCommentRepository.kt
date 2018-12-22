package com.summer.itis.summerproject.repository.json

import android.util.Log
import com.google.firebase.database.*
import com.summer.itis.summerproject.model.Comment
import com.summer.itis.summerproject.utils.Const
import com.summer.itis.summerproject.utils.RxUtils
import io.reactivex.Single
import java.util.ArrayList
import java.util.HashMap


class CardCommentRepository() {

    val databaseReference: DatabaseReference

    private val TABLE_NAME = "card_comments"

    init {
        this.databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)
    }

    fun readComment(pointId: String): DatabaseReference {
        return databaseReference.child(pointId)
    }

    fun deleteComment(pointId: String) {
        databaseReference.child(pointId).removeValue()
    }

    fun updateComment(comment: Comment) {
        val updatedValues = HashMap<String, Any>()
//        databaseReference.child(comment.id).updateChildren(updatedValues)
    }

    fun getComments(testId: String): Single<List<Comment>> {
        val single: Single<List<Comment>> = Single.create { e ->
            val query: Query = databaseReference.child(testId)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val comments: MutableList<Comment> = ArrayList()
                    for (postSnapshot in dataSnapshot.children) {
                        comments.add(postSnapshot.getValue(Comment::class.java)!!)
                    }
                    e.onSuccess(comments)

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(Const.TAG_LOG, "loadPost:onCancelled", databaseError.toException())
                }
            })
        }
        return single.compose(RxUtils.asyncSingle())

    }

    fun createComment(testId: String, comment: Comment): Single<Boolean> {
        val single: Single<Boolean> = Single.create { e ->
            val key = databaseReference.child(testId).push().key
            comment.id = key
            databaseReference.child(testId).child(key!!).setValue(comment)
            e.onSuccess(true)
        }
        return single.compose(RxUtils.asyncSingle())
    }

}