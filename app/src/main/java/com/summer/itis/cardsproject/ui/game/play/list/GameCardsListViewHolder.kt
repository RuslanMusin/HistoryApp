package com.summer.itis.cardsproject.ui.game.play.list

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.item_game_card_small.view.*

class GameCardsListViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    val image: ImageView
//    val nameView: TextView

    init {
        image = itemView!!.iv_game_s_card
//        nameView = itemView!!.tv_game_s_card_name
    }
}
