package com.summer.itis.summerproject.ui.cards.cards_list.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.SearchView
import android.view.Menu
import com.summer.itis.summerproject.R
import com.summer.itis.summerproject.repository.json.UserRepository
import com.summer.itis.summerproject.ui.base.EasyNavigationBaseActivity
import com.summer.itis.summerproject.ui.cards.cards_list.adapter.CardsListPagerAdapter
import com.summer.itis.summerproject.utils.Const.MY_LIST
import com.summer.itis.summerproject.utils.Const.OFFICIAL_LIST
import com.summer.itis.summerproject.utils.Const.ONLINE_STATUS
import com.summer.itis.summerproject.utils.Const.USER_LIST
import kotlinx.android.synthetic.main.activity_cards_list.toolbar

class CardsListActivity : EasyNavigationBaseActivity() {

    private lateinit var mTabLayout: TabLayout
    private lateinit var mViewPager: ViewPager
    private lateinit var mPagerAdapter: PagerAdapter

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, CardsListActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatus(ONLINE_STATUS)
            waitEnemy()
            super.onCreate(savedInstanceState)
            init()
            supportActionBar(toolbar)

    }

    override fun getContentLayout(): Int {
        return R.layout.activity_cards_list
    }

    private fun init(){
        mTabLayout = findViewById(R.id.tab_layout)
        mViewPager = findViewById(R.id.pager)
        mPagerAdapter = CardsListPagerAdapter(
                supportFragmentManager)
        mViewPager.adapter = mPagerAdapter
        mTabLayout.setupWithViewPager(mViewPager)
    }



}
