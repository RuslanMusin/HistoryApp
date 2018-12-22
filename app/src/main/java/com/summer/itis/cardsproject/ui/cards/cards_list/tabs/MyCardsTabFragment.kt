package com.summer.itis.summerproject.ui.cards.cards_list.tabs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.summer.itis.summerproject.model.AbstractCard
import com.summer.itis.summerproject.ui.base.BaseAdapter
import com.summer.itis.summerproject.ui.cards.card_item.CardActivity
import com.summer.itis.summerproject.ui.cards.cards_info.CardsActivity
import com.summer.itis.summerproject.utils.Const
import com.summer.itis.summerproject.utils.Const.USER_ABSTRACT_TESTS
import java.util.ArrayList

/**
 * Created by Home on 10.07.2018.
 */
class MyCardsTabFragment(): AbstractCardsTabFragment(){

    companion object {
        fun newInstance(): MyCardsTabFragment {
            val fragment = MyCardsTabFragment()
            fragment.type = Const.USER_LIST_CARDS
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardsPresenter.getUserAbstractCardsList()
    }

    override fun showDetails(card: AbstractCard) {
        CardActivity.start(context!!,card,USER_ABSTRACT_TESTS)
    }

    override fun onItemClick(item: AbstractCard) {
        cardsPresenter.onItemClick(item)
    }

    override fun showItems(items: List<AbstractCard>) {
        cards = items as ArrayList<AbstractCard>
        mRecyclerViewAdapter.changeDataSet(items)
    }

    override fun handleError(error: Throwable) {
        Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
    }

}