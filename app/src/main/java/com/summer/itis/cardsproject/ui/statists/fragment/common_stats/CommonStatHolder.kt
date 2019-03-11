package com.summer.itis.cardsproject.ui.statists.fragment.common_stats

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.LeaderStat
import com.summer.itis.cardsproject.model.UserEpoch
import com.summer.itis.cardsproject.utils.Const.DOUBLE_FORM
import kotlinx.android.synthetic.main.item_common_stat.view.*

class CommonStatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: UserEpoch) {
        itemView.tv_name.text = item.epoch?.name
        itemView.tv_ge.text = itemView.context.getString(R.string.ke_arg, DOUBLE_FORM.format(item.ke))
        itemView.tv_ge_change.text = itemView.context.getString(R.string.ke_change_arg, DOUBLE_FORM.format(item.keSub))
        itemView.tv_right.text = itemView.context.getString(R.string.right_arg, item.right)
        itemView.tv_wrong.text = itemView.context.getString(R.string.wrong_arg, item.wrong)
    }


    companion object {

        fun create(parent: ViewGroup): CommonStatHolder {
            val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_common_stat, parent, false);
            val holder = CommonStatHolder(view)
            return holder
        }
    }
}