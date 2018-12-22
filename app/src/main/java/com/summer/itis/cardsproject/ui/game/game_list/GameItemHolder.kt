package com.summer.itis.cardsproject.ui.game.game_list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.game.Lobby
import com.summer.itis.cardsproject.model.game.LobbyPlayerData
import com.summer.itis.cardsproject.utils.Const.MAX_LENGTH
import com.summer.itis.cardsproject.utils.Const.MORE_TEXT
import kotlinx.android.synthetic.main.item_game.view.*

class GameItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: Lobby) {
        itemView.tv_name.text = item.title

        itemView.tv_description.text = "Кол-во карт: ${item.cardNumber}"

        if (item.photoUrl != null) {
            Glide.with(itemView.iv_cover.context)
                    .load(item.photoUrl)
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

    companion object {

        private val MAX_LENGTH = 80
        private val MORE_TEXT = "..."

        fun create(parent: ViewGroup): GameItemHolder {
            val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false);
            val holder = GameItemHolder(view)
            return holder
        }
    }
}
