package com.summer.itis.cardsproject.ui.statists.fragment.leader_stats

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.LeaderStat
import kotlinx.android.synthetic.main.item_leader_stat.view.*

class LeaderStatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: LeaderStat) {
        itemView.tv_name.text = item.name
        itemView.tv_level.text = item.level.toString()
        itemView.tv_kg.text = item.kg.toString()
        itemView.tv_win.text = item.win.toString()
        itemView.tv_lose.text = item.lose.toString()
    }


    companion object {

        fun create(parent: ViewGroup): LeaderStatHolder {
            val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_leader_stat, parent, false);
            val holder = LeaderStatHolder(view)
            return holder
        }
    }
}
