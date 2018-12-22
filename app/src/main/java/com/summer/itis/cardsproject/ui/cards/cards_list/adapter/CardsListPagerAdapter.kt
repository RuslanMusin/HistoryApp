package com.summer.itis.summerproject.ui.cards.cards_list.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import com.summer.itis.summerproject.ui.cards.cards_list.tabs.AllCardsTabFragment
import com.summer.itis.summerproject.ui.cards.cards_list.tabs.MyCardsTabFragment

/**
 * Created by Home on 10.07.2018.
 */

class CardsListPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm){

    private val NUMOFTABS = 2
    val TABALLNAME = "All"
    val TABMYNAME = "My"

    override fun getItem(position: Int): Fragment? {
        return when (position){
            0 -> AllCardsTabFragment.newInstance()
            1 -> MyCardsTabFragment.newInstance()
            else -> {
                Log.d("CardsListPagerAdapter","can be 0 or 1, not this numbers")
                null
            }
        }
    }

    override fun getCount(): Int {
        return NUMOFTABS
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> TABALLNAME
            1 -> TABMYNAME
            else -> {
                Log.d("CardsListPagerAdapter","can be 0 or 1, not this numbers")
                null
            }
        }
    }
}