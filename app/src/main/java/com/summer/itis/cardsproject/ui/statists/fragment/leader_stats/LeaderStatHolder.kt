package com.summer.itis.cardsproject.ui.statists.fragment.leader_stats

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.LeaderStat
import com.summer.itis.cardsproject.utils.Const
import kotlinx.android.synthetic.main.item_leader_stat.view.*

class LeaderStatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: LeaderStat) {
        itemView.tv_name.text = item.name
        itemView.tv_level.text = itemView.context.getString(R.string.level, item.level)
        itemView.tv_kg.text = itemView.context.getString(R.string.kg_arg, Const.DOUBLE_FORM.format(item.kg))
        itemView.tv_win.text = itemView.context.getString(R.string.win_arg, item.win)
        itemView.tv_lose.text = itemView.context.getString(R.string.lose_arg, item.lose)
    }


    companion object {

        fun create(parent: ViewGroup): LeaderStatHolder {
            val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_leader_stat, parent, false);
            val holder = LeaderStatHolder(view)
            return holder
        }
    }
}
