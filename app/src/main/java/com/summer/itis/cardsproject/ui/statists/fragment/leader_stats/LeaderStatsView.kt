package com.summer.itis.cardsproject.ui.statists.fragment.leader_stats

import com.arellomobile.mvp.MvpView
import com.summer.itis.cardsproject.model.LeaderStat
import com.summer.itis.cardsproject.ui.statists.fragment.BaseRecyclerView

interface LeaderStatsView: MvpView, BaseRecyclerView<LeaderStat> {

    fun showStats(themes: List<LeaderStat>)
}