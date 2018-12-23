package com.summer.itis.cardsproject.ui.statists.fragment.common_stats

import com.arellomobile.mvp.MvpView
import com.summer.itis.cardsproject.model.LeaderStat
import com.summer.itis.cardsproject.model.UserEpoch
import com.summer.itis.cardsproject.ui.statists.fragment.BaseRecyclerView

interface CommonStatsView: MvpView, BaseRecyclerView<UserEpoch> {

    fun showStats(themes: List<UserEpoch>)
}