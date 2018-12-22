package com.summer.itis.cardsproject.ui.cards.cards_states

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.summer.itis.cardsproject.model.AbstractCard
import com.summer.itis.cardsproject.model.Card
import com.summer.itis.cardsproject.ui.cards.card_states.fragment.CardStatesFragment

/**
 * Created by Home on 11.07.2018.
 */
class CardsStatesPagerAdapter(fm : FragmentManager,var cards : ArrayList<Card>, var card: AbstractCard) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return CardStatesFragment.newInstance(cards[position], card)
    }

    override fun getCount(): Int {
        return cards.size
    }

    fun setNewCards(cards: ArrayList<Card>){
        this.cards = cards
        notifyDataSetChanged()
    }
}