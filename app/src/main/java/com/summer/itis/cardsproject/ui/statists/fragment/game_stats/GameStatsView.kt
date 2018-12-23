package com.summer.itis.cardsproject.ui.statists.fragment.game_stats

import com.arellomobile.mvp.MvpView
import com.summer.itis.cardsproject.model.LeaderStat
import com.summer.itis.cardsproject.model.UserEpoch
import com.summer.itis.cardsproject.ui.statists.fragment.BaseRecyclerView

interface GameStatsView: MvpView, BaseRecyclerView<UserEpoch> {

    fun showStats(themes: List<UserEpoch>)
}