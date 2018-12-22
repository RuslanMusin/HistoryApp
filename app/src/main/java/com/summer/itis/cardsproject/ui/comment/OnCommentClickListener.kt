package com.summer.itis.cardsproject.ui.comment

import com.summer.itis.cardsproject.model.Comment
import com.summer.itis.cardsproject.ui.base.BaseAdapter

interface OnCommentClickListener : BaseAdapter.OnItemClickListener<Comment> {

    fun onReplyClick(position: Int)

    fun onAuthorClick(position: String)

    fun setComments(comments: List<Comment>)

    fun addComment(comment: Comment)
}
