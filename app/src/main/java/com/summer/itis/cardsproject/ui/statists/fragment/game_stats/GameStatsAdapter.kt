package com.summer.itis.cardsproject.ui.statists.fragment.game_stats

import android.view.ViewGroup
import com.summer.itis.cardsproject.model.LeaderStat
import com.summer.itis.cardsproject.model.UserEpoch
import com.summer.itis.cardsproject.ui.base.BaseAdapter
import com.summer.itis.cardsproject.ui.statists.fragment.leader_stats.LeaderStatHolder

class GameStatsAdapter(items: MutableList<UserEpoch>) : BaseAdapter<UserEpoch, GameStatHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameStatHolder {
        return GameStatHolder.create(parent)
    }

    override fun onBindViewHolder(holder: GameStatHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        holder.bind(item)
    }
}