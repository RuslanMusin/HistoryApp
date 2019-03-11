package com.summer.itis.cardsproject.ui.epoch

import android.view.ViewGroup
import com.summer.itis.cardsproject.model.Epoch
import com.summer.itis.cardsproject.model.UserEpoch
import com.summer.itis.cardsproject.ui.base.BaseAdapter
import com.summer.itis.cardsproject.ui.statists.fragment.game_stats.GameStatHolder

class EpochAdapter(items: MutableList<Epoch>) : BaseAdapter<Epoch, EpochItemHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpochItemHolder {
        return EpochItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: EpochItemHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        holder.bind(item)
    }
}