package com.summer.itis.cardsproject.ui.statists.fragment.common_stats

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.LeaderStat
import com.summer.itis.cardsproject.model.UserEpoch
import kotlinx.android.synthetic.main.item_common_stat.view.*

class CommonStatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: UserEpoch) {
        itemView.tv_name.text = item.epoch.name
        itemView.tv_ge.text = item.ke.toString()
        itemView.tv_ge_change.text = item.keSub.toString()
        itemView.tv_right.text = item.right.toString()
        itemView.tv_wrong.text = item.wrong.toString()
    }


    companion object {

        fun create(parent: ViewGroup): CommonStatHolder {
            val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_common_stat, parent, false);
            val holder = CommonStatHolder(view)
            return holder
        }
    }
}