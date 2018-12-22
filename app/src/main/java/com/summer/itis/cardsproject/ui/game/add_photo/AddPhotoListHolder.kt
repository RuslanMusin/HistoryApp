package com.summer.itis.cardsproject.ui.game.add_photo

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.db_dop_models.PhotoItem
import com.summer.itis.cardsproject.ui.cards.add_card_list.AddCardListHolder
import com.summer.itis.cardsproject.utils.ImageLoadHelper
import kotlinx.android.synthetic.main.item_member.view.*

class AddPhotoListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: PhotoItem) {
        Glide.with(itemView.iv_cover.context)
                .load(item.photoUrl)
                .into(itemView.iv_cover)
    }


    companion object {

        fun create(context: Context): AddPhotoListHolder {
            val view = View.inflate(context, R.layout.item_photo, null)
            return AddPhotoListHolder(view)
        }
    }
}
