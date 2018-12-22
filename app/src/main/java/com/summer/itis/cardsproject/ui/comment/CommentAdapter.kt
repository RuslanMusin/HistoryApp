package com.summer.itis.summerproject.ui.comment

import android.view.ViewGroup
import com.summer.itis.summerproject.model.Comment
import com.summer.itis.summerproject.ui.base.BaseAdapter


class CommentAdapter(items: MutableList<Comment>, private val listener: OnCommentClickListener) : BaseAdapter<Comment, CommentItemHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentItemHolder {
        return CommentItemHolder.create(parent.context, listener)
    }

    override fun onBindViewHolder(holder: CommentItemHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        holder.bind(item)
    }
}
