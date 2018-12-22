package com.summer.itis.cardsproject.ui.game.play.change_list

import android.util.Log
import android.view.ViewGroup
import com.summer.itis.cardsproject.model.Card
import com.summer.itis.cardsproject.ui.base.BaseAdapter
import com.summer.itis.cardsproject.utils.Const.TAG_LOG
import com.summer.itis.cardsproject.utils.getRandom

class GameChangeListAdapter(items: MutableList<Card>, val allCards: MutableList<Card>, val size: Int, val onStop: () -> Unit) : BaseAdapter<Card, GameChangeListViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameChangeListViewHolder {
//        return TestItemHolder.create(parent.context)
        return GameChangeListViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: GameChangeListViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        holder.itemView.setOnLongClickListener{t ->
            Log.d(TAG_LOG,"onLongClicked")
            allCards.getRandom()?.let {
                allCards.remove(it)
                items.toMutableList().let { cards ->
                    cards.removeAt(position)
                    cards.add(position, it)
                    changeDataSet(cards)
                }
                if(size - allCards.size >= 2 || allCards.size == 0) {
                    onStop()
                }
            }

            true
        }
        holder.bind(item)
    }

}