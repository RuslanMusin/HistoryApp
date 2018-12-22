package com.summer.itis.summerproject.ui.comment

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.summer.itis.summerproject.R
import com.summer.itis.summerproject.R.id.*
import com.summer.itis.summerproject.model.Comment
import com.summer.itis.summerproject.ui.widget.ExpandableTextView
import com.summer.itis.summerproject.utils.Const.STUB_PATH
import com.summer.itis.summerproject.utils.FormatterUtil
import kotlinx.android.synthetic.main.item_comment.view.*

class CommentItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

   /* private val avatarImageView: ImageView
    private val commentTextView: ExpandableTextView
    private val dateTextView: TextView
    private val replyView: ImageView
*/
    private var commentClickListener: OnCommentClickListener? = null

   /* init {

        avatarImageView = itemView.findViewById<View>(R.id.avatarImageView) as ImageView
        commentTextView = itemView.findViewById<View>(R.id.commentText) as ExpandableTextView
        dateTextView = itemView.findViewById<View>(R.id.dateTextView) as TextView
        replyView = itemView.findViewById(R.id.iv_reply)
    }*/

    fun bind(comment: Comment) {
        val authorId = comment.authorId

        itemView.tv_name.text = comment.authorName
        itemView.expand_text_view.text = comment.text

        val date = FormatterUtil.getRelativeTimeSpanString(itemView.context, comment.createdDate)
        itemView.tv_date.setText(date)

        itemView.iv_reply.setOnClickListener { commentClickListener!!.onReplyClick(adapterPosition) }

        itemView.iv_cover.setOnClickListener { authorId?.let { it1 -> commentClickListener!!.onAuthorClick(it1) } }

        fillComment(comment)

    }

    private fun fillComment(comment: Comment) {
      /*  val contentString = SpannableStringBuilder(comment.authorName
                + "   " + comment.text)
        comment.authorName?.length?.let {
            contentString.setSpan(ForegroundColorSpan(ContextCompat.getColor(commentTextView.getContext(), R.color.highlight_text)),
                0, it, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }*/

//        commentTextView.text = contentString

        if(!comment.authorPhotoUrl.equals(STUB_PATH)) {
            val imageReference = comment.authorPhotoUrl?.let { FirebaseStorage.getInstance().reference.child(it) }

            Glide.with(itemView.getContext())
                    .load(imageReference)
                    .into(itemView.iv_cover)
        } else {
            Glide.with(itemView.getContext())
                    .load(R.drawable.ic_account_circle_black_24dp)
                    .into(itemView.iv_cover)
        }
    }

    private fun cutLongDescription(description: String): String {
        return if (description.length < MAX_LENGTH) {
            description
        } else {
            description.substring(0, MAX_LENGTH - MORE_TEXT.length) + MORE_TEXT
        }
    }

    private fun fillComment(userName: String, comment: String, commentTextView: ExpandableTextView) {
        val contentString: Spannable = SpannableStringBuilder("$userName   $comment")
        contentString.setSpan(ForegroundColorSpan(ContextCompat.getColor(commentTextView.getContext(), R.color.highlight_text)),
                0, userName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        commentTextView.text = contentString
    }

    companion object {

        private val MAX_LENGTH = 80
        private val MORE_TEXT = "..."

        fun create(context: Context, commentClickListener: OnCommentClickListener): CommentItemHolder {
            val view = View.inflate(context, R.layout.item_comment, null)
            val holder = CommentItemHolder(view)
            holder.commentClickListener = commentClickListener
            return holder
        }
    }
}
