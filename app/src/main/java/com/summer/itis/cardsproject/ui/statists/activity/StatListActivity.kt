package com.summer.itis.cardsproject.ui.statists.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.Test
import com.summer.itis.cardsproject.repository.json.UserRepository
import com.summer.itis.cardsproject.ui.base.NavigationBaseActivity
import com.summer.itis.cardsproject.ui.statists.fragment.common_stats.CommonStatsFragment
import com.summer.itis.cardsproject.ui.statists.fragment.game_stats.GameStatsFragment
import com.summer.itis.cardsproject.ui.statists.fragment.leader_stats.LeaderStatsFragment
import com.summer.itis.cardsproject.ui.tests.test_item.TestActivity
import com.summer.itis.cardsproject.ui.tests.test_list.TestAdapter
import com.summer.itis.cardsproject.ui.tests.test_list.fragment.TestListFragment
import com.summer.itis.cardsproject.ui.tests.test_list.test.TestListPresenter
import com.summer.itis.cardsproject.ui.tests.test_list.test.TestListView
import com.summer.itis.cardsproject.utils.Const
import io.reactivex.disposables.Disposable
import java.util.ArrayList

class StatListActivity : NavigationBaseActivity(), StatListView {

    private var toolbar: Toolbar? = null
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null

    @InjectPresenter
    lateinit var presenter: StatListPresenter

    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatus(Const.ONLINE_STATUS)
        waitEnemy()
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.container)
        layoutInflater.inflate(R.layout.activity_test_pager, contentFrameLayout)

        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar?.title = getString(R.string.statistics)
        supportActionBar(toolbar!!)

        viewPager = findViewById<View>(R.id.viewpager) as ViewPager
        setupViewPager(viewPager!!)

        tabLayout = findViewById<View>(R.id.tabs) as TabLayout
        tabLayout!!.setupWithViewPager(viewPager)

        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        setTabListener()

    }

    private fun setTabListener() {
        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Log.d(Const.TAG_LOG, "on tab selected")
                viewPager!!.currentItem = tab.position
//                this@TestListActivity.changeAdapter(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }


    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(CommonStatsFragment.newInstance(), Const.COMMON_LIST)
        adapter.addFragment(GameStatsFragment.newInstance(), Const.MY_GAME_LIST)
        adapter.addFragment(LeaderStatsFragment.newInstance(), Const.LEADER_LIST)
        viewPager.adapter = adapter
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val fragmentList = ArrayList<Fragment>()
        private val fragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList.add(fragment)
            fragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentTitleList[position]
        }
    }

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, StatListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }
}
