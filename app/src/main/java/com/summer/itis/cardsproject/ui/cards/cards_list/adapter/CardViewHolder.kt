package com.summer.itis.summerproject.ui.cards.cards_list.adapter

import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.summer.itis.summerproject.R
import android.content.Context
import android.support.annotation.NonNull
import com.bumptech.glide.Glide
import com.summer.itis.summerproject.R.id.tv_desc
import com.summer.itis.summerproject.R.id.tv_name
import com.summer.itis.summerproject.model.AbstractCard
import com.summer.itis.summerproject.ui.tests.test_list.TestItemHolder
import com.summer.itis.summerproject.utils.ApplicationHelper
import com.summer.itis.summerproject.utils.ApplicationHelper.Companion.cutLongDescription
import com.summer.itis.summerproject.utils.Const.MAX_LENGTH
import kotlinx.android.synthetic.main.item_member.view.*

/**
 * Created by Home on 10.07.2018.
 */

class CardViewHolder(itemView: View) : ViewHolder(itemView) {

    companion object {

        const val MAX_LENGTH = 80
        fun create(context: Context): CardViewHolder {
            val view = View.inflate(context, R.layout.item_member, null)
            val holder = CardViewHolder(view)
            return holder
        }
    }


    fun bind(@NonNull item: AbstractCard) {
        itemView.tv_name.text = item.name
        itemView.tv_description.text = item.description?.let { cutLongDescription(it, MAX_LENGTH) }
        if(item.photoUrl != null) {
            Glide.with(itemView.context)
                    .load(item.photoUrl)
                    .into(itemView.iv_cover)
        }
    }
}