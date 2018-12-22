package com.summer.itis.cardsproject.ui.cards.cards_info

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.widget.FrameLayout
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.AbstractCard
import com.summer.itis.cardsproject.ui.base.EasyNavigationBaseActivity
import com.summer.itis.cardsproject.ui.base.NavigationBaseActivity
import kotlinx.android.synthetic.main.activity_cards.toolbar
import java.util.ArrayList

class CardsActivity : NavigationBaseActivity() {

    lateinit var mViewPager: ViewPager
    lateinit var mPagerAdapter: CardsPagerAdapter
    lateinit var cards: ArrayList<AbstractCard>

    companion object {
        fun start(activity: Context,card: AbstractCard,cards : ArrayList<AbstractCard>,tag : String){
            val intent = Intent(activity, CardsActivity::class.java)
            intent.putExtra("CARD", card)
            intent.putParcelableArrayListExtra("CARDS", cards)
            intent.putExtra("TAG", tag)
            activity?.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentFrameLayout = findViewById<FrameLayout>(R.id.container)
        layoutInflater.inflate(getContentLayout(), contentFrameLayout)

        val card = intent.getParcelableExtra<AbstractCard>("CARD")
        cards = intent.getParcelableArrayListExtra("CARDS")
        var pos = getPosOfCard(card)
        mViewPager = findViewById(R.id.pager)
        mPagerAdapter = CardsPagerAdapter(supportFragmentManager,cards,intent.getStringExtra("TAG"))
        mViewPager.adapter = mPagerAdapter
        mViewPager.setCurrentItem(pos)
        mPagerAdapter.notifyDataSetChanged()
        setSupportActionBar(toolbar)
        setBackArrow(toolbar)
    }

    fun getContentLayout(): Int {
        return R.layout.activity_cards
    }

    private fun getPosOfCard(card: AbstractCard): Int{
        var pos: Int = 0
        for(x in cards){
            if(x.id == card.id){
                pos = cards.indexOf(x)
            }
        }
        return pos
    }
}
