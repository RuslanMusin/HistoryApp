package com.summer.itis.summerproject.ui.member.member_list

import android.view.ViewGroup

import com.summer.itis.summerproject.model.User
import com.summer.itis.summerproject.ui.base.BaseAdapter

class MemberAdapter(items: MutableList<User>) : BaseAdapter<User, MemberItemHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberItemHolder {
        return MemberItemHolder.create(parent.context)
    }

    override fun onBindViewHolder(holder: MemberItemHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        holder.bind(item)
    }
}
