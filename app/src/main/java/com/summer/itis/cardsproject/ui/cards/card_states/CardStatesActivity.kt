package com.summer.itis.summerproject.ui.cards.card_states

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.summer.itis.summerproject.R
import com.summer.itis.summerproject.model.AbstractCard
import com.summer.itis.summerproject.model.Card
import com.summer.itis.summerproject.repository.RepositoryProvider
import com.summer.itis.summerproject.ui.base.*
import com.summer.itis.summerproject.ui.cards.cards_states.CardsStatesPagerAdapter
import com.summer.itis.summerproject.ui.tests.ChangeToolbarListener
import com.summer.itis.summerproject.utils.ApplicationHelper
import com.summer.itis.summerproject.utils.Const.ONLINE_STATUS
import com.summer.itis.summerproject.utils.Const.TAG_LOG
import kotlinx.android.synthetic.main.back_forward.*
import java.util.ArrayList

class CardStatesActivity : NavigationBaseActivity(), ChangeToolbarListener, ViewPagerView {

    private lateinit var mViewPager: ViewPager
    private lateinit var mPagerAdapter: CardsStatesPagerAdapter
    private lateinit var card: AbstractCard

    companion object {
        fun start(context: Context,card : AbstractCard){
            var intent = Intent(context,CardStatesActivity::class.java)
            intent.putExtra("CARD", card)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatus(ONLINE_STATUS)
        waitEnemy()
        super.onCreate(savedInstanceState)
        val contentFrameLayout = findViewById<FrameLayout>(R.id.container)
        layoutInflater.inflate(getContentLayout(), contentFrameLayout)

        card = intent.getParcelableExtra("CARD")
        mViewPager = findViewById(R.id.pager)
        mPagerAdapter = CardsStatesPagerAdapter(supportFragmentManager, ArrayList(), card)
        mViewPager.adapter = mPagerAdapter
        getCardsStates()
        card.name?.let { setToolbarTitle(it) }
        setSupportActionBar(test_toolbar)
        btn_back.setOnClickListener(this)
        btn_forward.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
    }

    fun getContentLayout(): Int {
        return R.layout.activity_card_states
    }

    fun setCardsStates(cards: ArrayList<Card>){
        mPagerAdapter.setNewCards(cards)
    }

    fun getCardsStates(){
        RepositoryProvider
                .cardRepository
                .findDefaultAbstractCardStates(card.id!!)
                .subscribe({it -> setCardsStates(it as ArrayList<Card>)})
    }

    override fun changeToolbar(tag: String, title: String) {
    }

    override fun showOk(boolean: Boolean) {
    }

    override fun onClick(v: View?) {
        when(v?.id) {

            R.id.btn_back -> {
                Log.d(TAG_LOG,"back")
                changePosition(true)
            }

            R.id.btn_forward -> {
                Log.d(TAG_LOG,"forward")
                changePosition(false)
            }

            R.id.btn_cancel -> {
                Log.d(TAG_LOG,"cancel")
                onBackPressed()
            }
        }
    }

    override fun setToolbarTitle(title: String) {
        toolbar_title.text = title
    }

    override fun changePosition(isBack: Boolean) {
        var position: Int = mViewPager.currentItem
        Log.d(TAG_LOG,"position = $position")
        if(isBack) {
            if(position > 0) {
                position--
            } else {
                onBackPressed()
            }
        } else {
            if(position < mPagerAdapter.cards.size) {
                position++
            }
        }
        mViewPager.currentItem = position
    }

}
