package com.summer.itis.cardsproject.ui.tests.test_list.test

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
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.userRepository
import com.summer.itis.cardsproject.repository.json.UserRepository
import com.summer.itis.cardsproject.repository.json.UserRepository.Companion.currentId
import com.summer.itis.cardsproject.ui.base.NavigationBaseActivity
import com.summer.itis.cardsproject.ui.tests.test_item.TestActivity
import com.summer.itis.cardsproject.ui.tests.test_list.TestAdapter
import com.summer.itis.cardsproject.ui.tests.test_list.fragment.TestListFragment
import com.summer.itis.cardsproject.utils.Const.EDIT_STATUS
import com.summer.itis.cardsproject.utils.Const.MY_LIST
import com.summer.itis.cardsproject.utils.Const.OFFICIAL_LIST
import com.summer.itis.cardsproject.utils.Const.ONLINE_STATUS
import com.summer.itis.cardsproject.utils.Const.TAG_LOG
import com.summer.itis.cardsproject.utils.Const.USER_LIST
import io.reactivex.disposables.Disposable

class TestListActivity : NavigationBaseActivity(), TestListView {

    private var toolbar: Toolbar? = null
    private var progressBar: ProgressBar? = null
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    private var adapter: TestAdapter? = null

    @InjectPresenter
    lateinit var presenter: TestListPresenter

    private var isLoading = false
    private var currentType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatus(ONLINE_STATUS)
        waitEnemy()
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.container)
        layoutInflater.inflate(R.layout.activity_test_pager, contentFrameLayout)

        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar?.title = getString(R.string.tests)
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
                Log.d(TAG_LOG, "on tab selected")
                viewPager!!.currentItem = tab.position
                this@TestListActivity.changeAdapter(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    override fun changeAdapter(position: Int) {
        val fragment = (viewPager!!.adapter as ViewPagerAdapter).getFragmentForChange(position)
        fragment.changeDataInAdapter()
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(TestListFragment.newInstance(OFFICIAL_LIST, this), OFFICIAL_LIST)
        adapter.addFragment(TestListFragment.newInstance(USER_LIST, this), USER_LIST)
        adapter.addFragment(TestListFragment.newInstance(MY_LIST, this),MY_LIST)
        this.currentType = OFFICIAL_LIST
        viewPager.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)

        var searchView: SearchView? = null
        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
        }
        if (searchView != null) {
            val finalSearchView = searchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String): Boolean {
                    when (currentType) {
                        OFFICIAL_LIST -> presenter!!.loadOfficialTestsByQUery(query)

                        USER_LIST -> presenter!!.loadUserTestsByQUery(query, UserRepository.currentId)

                        MY_LIST -> presenter!!.loadMyTestsByQUery(query, UserRepository.currentId)
                    }
                    if (!finalSearchView.isIconified) {
                        finalSearchView.isIconified = true
                    }
                    searchItem!!.collapseActionView()
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    return false
                }
            })
        }
        return super.onCreateOptionsMenu(menu)
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        fun getFragmentForChange(position: Int): TestListFragment {
            return mFragmentList[position] as TestListFragment
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

    override fun onItemClick(item: Test) {
        presenter!!.onItemClick(item)
    }

    override fun handleError(error: Throwable) {
        Log.d(TAG_LOG, "error = " + error.message)
        error.printStackTrace()
        Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
    }

    override fun changeDataSet(users: List<Test>) {
        adapter!!.changeDataSet(users)
    }

    override fun setAdapter(adapter: TestAdapter) {
        Log.d(TAG_LOG, "set adapter")
        Log.d(TAG_LOG, "type adapter =  " + currentType!!)
        this.adapter = adapter
    }

    override fun loadOfficialTests() {
        Log.d(TAG_LOG, "load requests")
        presenter!!.loadOfficialTests()
    }

    override fun loadUserTests() {
        Log.d(TAG_LOG, "load friends")
        presenter!!.loadUserTests(currentId)
    }

    override fun loadMyTests(userId: String) {
        Log.d(TAG_LOG, "load friends")
        presenter!!.loadMyTests(currentId)
    }


    override fun setProgressBar(progressBar: ProgressBar?) {
        this.progressBar = progressBar
        Log.d(TAG_LOG,"set proggress  and type = $currentType}")
    }

    override fun setNotLoading() {
        isLoading = false
    }

    override fun showLoading(disposable: Disposable) {
        Log.d(TAG_LOG,"show loading  and type = $currentType}")
        progressBar!!.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        Log.d(TAG_LOG,"hide loading and type = $currentType}")
        progressBar!!.visibility = View.GONE
    }

    override fun showDetails(item: Test) {
        Log.d(TAG_LOG,"show test act")
        TestActivity.start(this, item)
    }

    override fun loadNextElements(i: Int) {
//        presenter!!.loadNextElements(i)
    }

    override fun setCurrentType(type: String) {
        Log.d(TAG_LOG, "current type = $type")
        this.currentType = type
    }

    fun getCurrentType(): String? {
        return currentType
    }

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, TestListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }
}
