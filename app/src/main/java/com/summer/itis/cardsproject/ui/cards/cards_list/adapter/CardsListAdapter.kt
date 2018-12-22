package com.summer.itis.cardsproject.ui.cards.cards_list.adapter

import android.view.ViewGroup
import com.summer.itis.cardsproject.model.AbstractCard
import com.summer.itis.cardsproject.ui.base.BaseAdapter

/**
 * Created by Home on 10.07.2018.
 */
class CardsListAdapter(var list: List<AbstractCard>):
        BaseAdapter<AbstractCard, CardViewHolder>(list.toMutableList()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder.create(parent.context)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        var card = getItem(position)
        holder.bind(card)
    }
}