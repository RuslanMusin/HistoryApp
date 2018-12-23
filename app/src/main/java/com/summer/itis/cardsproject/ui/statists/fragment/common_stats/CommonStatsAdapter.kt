package com.summer.itis.cardsproject.ui.statists.fragment.common_stats

import android.view.ViewGroup
import com.summer.itis.cardsproject.model.LeaderStat
import com.summer.itis.cardsproject.model.UserEpoch
import com.summer.itis.cardsproject.ui.base.BaseAdapter
import com.summer.itis.cardsproject.ui.statists.fragment.leader_stats.LeaderStatHolder

class CommonStatsAdapter(items: MutableList<UserEpoch>) : BaseAdapter<UserEpoch, CommonStatHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonStatHolder {
        return CommonStatHolder.create(parent)
    }

    override fun onBindViewHolder(holder: CommonStatHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        holder.bind(item)
    }
}