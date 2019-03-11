package com.summer.itis.cardsproject.ui.epoch

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.Epoch
import com.summer.itis.cardsproject.model.UserEpoch
import com.summer.itis.cardsproject.ui.base.BaseAdapter
import com.summer.itis.cardsproject.ui.statists.fragment.game_stats.GameStatHolder
import kotlinx.android.synthetic.main.item_game_stat.view.*

class EpochItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: Epoch) {
        itemView.tv_name.text = item.name
    }


    companion object {

        fun create(parent: ViewGroup): EpochItemHolder {
            val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_epoch, parent, false);
            val holder = EpochItemHolder(view)
            return holder
        }
    }
}