package com.summer.itis.summerproject.ui.comment

import com.summer.itis.summerproject.model.Comment
import com.summer.itis.summerproject.ui.base.BaseAdapter

interface OnCommentClickListener : BaseAdapter.OnItemClickListener<Comment> {

    fun onReplyClick(position: Int)

    fun onAuthorClick(position: String)

    fun setComments(comments: List<Comment>)

    fun addComment(comment: Comment)
}
