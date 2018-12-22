package com.summer.itis.summerproject.ui.cards.add_card_list

import android.view.ViewGroup

import com.summer.itis.summerproject.model.pojo.opensearch.Item
import com.summer.itis.summerproject.ui.base.BaseAdapter

class AddCardListAdapter(items: MutableList<Item>) : BaseAdapter<Item, AddCardListHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddCardListHolder {
        return AddCardListHolder.create(parent.context)
    }

    override fun onBindViewHolder(holder: AddCardListHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        holder.bind(item)
    }
}
