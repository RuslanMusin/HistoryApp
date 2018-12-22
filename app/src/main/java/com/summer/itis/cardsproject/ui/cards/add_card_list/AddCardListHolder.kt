package com.summer.itis.cardsproject.ui.cards.add_card_list

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.R.string.name
import com.summer.itis.cardsproject.model.pojo.opensearch.Item
import com.summer.itis.cardsproject.utils.ImageLoadHelper
import kotlinx.android.synthetic.main.item_member.view.*


class AddCardListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: Item) {
        itemView.tv_name.text = item.text!!.content
        val desc = item.description!!.content
        if (desc != null) {
            itemView.tv_description.text = cutLongDescription(desc)
        } else {
            itemView.tv_description.text = itemView.context.getText(R.string.description_default)
        }
        if (item.image != null) {
            /* if(items.getPhotoUrl().equals(String.valueOf(R.drawable.book_default))) {
                ImageLoadHelper.loadPictureByDrawableDefault(imageView,R.drawable.book_default);
            } else {
                ImageLoadHelper.loadPicture(imageView, items.getPhotoUrl());
            }*/
            ImageLoadHelper.loadPicture(itemView.iv_cover, item.image!!.source!!)

        }
    }

    private fun cutLongDescription(description: String): String {
        return if (description.length < MAX_LENGTH) {
            description
        } else {
            description.substring(0, MAX_LENGTH - MORE_TEXT.length) + MORE_TEXT
        }
    }

    companion object {

        private val MAX_LENGTH = 80
        private val MORE_TEXT = "..."

        fun create(context: Context): AddCardListHolder {
            val view = View.inflate(context, R.layout.item_member, null)
            val holder = AddCardListHolder(view)
            return AddCardListHolder(view)
        }
    }
}
