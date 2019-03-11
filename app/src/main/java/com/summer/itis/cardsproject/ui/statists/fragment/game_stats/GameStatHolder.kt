package com.summer.itis.cardsproject.ui.statists.fragment.game_stats

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.LeaderStat
import com.summer.itis.cardsproject.model.UserEpoch
import com.summer.itis.cardsproject.utils.Const
import kotlinx.android.synthetic.main.item_game_stat.view.*

class GameStatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: UserEpoch) {
        itemView.tv_name.text = item.epoch?.name
        itemView.tv_ge.text = itemView.context.getString(R.string.ge_arg, Const.DOUBLE_FORM.format(item.ge))
        itemView.tv_ge_change.text = itemView.context.getString(R.string.ge_change_arg, Const.DOUBLE_FORM.format(item.geSub))
        itemView.tv_win.text = itemView.context.getString(R.string.win_arg, item.win)
        itemView.tv_lose.text = itemView.context.getString(R.string.lose_arg, item.lose)
    }


    companion object {

        fun create(parent: ViewGroup): GameStatHolder {
            val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_game_stat, parent, false);
            val holder = GameStatHolder(view)
            return holder
        }
    }
}