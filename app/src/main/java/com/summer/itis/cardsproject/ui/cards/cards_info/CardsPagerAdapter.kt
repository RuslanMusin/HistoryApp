package com.summer.itis.summerproject.ui.cards.cards_info

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.summer.itis.summerproject.model.AbstractCard

/**
 * Created by Home on 11.07.2018.
 */
class CardsPagerAdapter(fm : FragmentManager,var cards : List<AbstractCard>,var tag : String) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return CardFragment.newInstance(cards[position],tag)
    }

    override fun getCount(): Int {
        return cards.size
    }
}