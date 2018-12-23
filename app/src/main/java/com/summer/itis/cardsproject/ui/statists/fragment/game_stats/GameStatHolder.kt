package com.summer.itis.cardsproject.ui.statists.fragment.game_stats

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.LeaderStat
import com.summer.itis.cardsproject.model.UserEpoch
import kotlinx.android.synthetic.main.item_game_stat.view.*

class GameStatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: UserEpoch) {
        itemView.tv_name.text = item.epoch?.name
        itemView.tv_ge.text = item.ge.toString()
        itemView.tv_ge_change.text = item.geSub.toString()
        itemView.tv_win.text = item.win.toString()
        itemView.tv_lose.text = item.lose.toString()
    }


    companion object {

        fun create(parent: ViewGroup): GameStatHolder {
            val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_game_stat, parent, false);
            val holder = GameStatHolder(view)
            return holder
        }
    }
}