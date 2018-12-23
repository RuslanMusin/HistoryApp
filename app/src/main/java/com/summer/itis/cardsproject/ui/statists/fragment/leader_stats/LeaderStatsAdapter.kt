package com.summer.itis.cardsproject.ui.statists.fragment.leader_stats

import android.view.ViewGroup
import com.summer.itis.cardsproject.model.LeaderStat
import com.summer.itis.cardsproject.ui.base.BaseAdapter

class LeaderStatsAdapter(items: MutableList<LeaderStat>) : BaseAdapter<LeaderStat, LeaderStatHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderStatHolder {
        return LeaderStatHolder.create(parent)
    }

    override fun onBindViewHolder(holder: LeaderStatHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        holder.bind(item)
    }
}