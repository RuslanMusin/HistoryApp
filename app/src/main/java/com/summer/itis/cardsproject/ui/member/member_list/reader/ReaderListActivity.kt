package com.summer.itis.cardsproject.ui.member.member_list.reader


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
import com.summer.itis.cardsproject.model.User
import com.summer.itis.cardsproject.repository.json.UserRepository
import com.summer.itis.cardsproject.ui.base.NavigationBaseActivity
import com.summer.itis.cardsproject.ui.member.member_item.PersonalActivity
import com.summer.itis.cardsproject.ui.member.member_list.MemberAdapter
import com.summer.itis.cardsproject.ui.member.member_list.fragment.ReaderListFragment
import com.summer.itis.cardsproject.utils.Const.ADD_FRIEND
import com.summer.itis.cardsproject.utils.Const.FRIEND_LIST
import com.summer.itis.cardsproject.utils.Const.ONLINE_STATUS
import com.summer.itis.cardsproject.utils.Const.READER_LIST
import com.summer.itis.cardsproject.utils.Const.REMOVE_FRIEND
import com.summer.itis.cardsproject.utils.Const.REQUEST_LIST
import com.summer.itis.cardsproject.utils.Const.TAG_LOG
import io.reactivex.disposables.Disposable
import java.util.*


class ReaderListActivity : NavigationBaseActivity(), ReaderListView {

    private var toolbar: Toolbar? = null
    private var progressBar: ProgressBar? = null
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    private var adapter: MemberAdapter? = null

    @InjectPresenter
    lateinit var presenter: ReaderListPresenter

    private var isLoading = false
    private var currentType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatus(ONLINE_STATUS)
        waitEnemy()
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.container)
        layoutInflater.inflate(R.layout.activity_test_pager, contentFrameLayout)

        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        supportActionBar(toolbar!!)
        setToolbarTitle(getString(R.string.users))
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
                this@ReaderListActivity.changeAdapter(tab.position)
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
        adapter.addFragment(ReaderListFragment.newInstance(READER_LIST, this), getString(R.string.all_users))
        adapter.addFragment(ReaderListFragment.newInstance(FRIEND_LIST, this), getString(R.string.friends))
        adapter.addFragment(ReaderListFragment.newInstance(REQUEST_LIST, this),getString(R.string.requests))
        this.currentType = READER_LIST
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
                        READER_LIST -> presenter!!.loadReadersByQuery(query)

                        FRIEND_LIST -> presenter!!.loadUsersByQueryAndType(query, UserRepository.currentId, REMOVE_FRIEND)

                        REQUEST_LIST -> presenter!!.loadUsersByQueryAndType(query, UserRepository.currentId, ADD_FRIEND)
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

        fun getFragmentForChange(position: Int): ReaderListFragment {
            return mFragmentList[position] as ReaderListFragment
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

    override fun onItemClick(item: User) {
        presenter!!.onItemClick(item)
    }

    override fun handleError(error: Throwable) {
        Log.d(TAG_LOG, "error = " + error.message)
        error.printStackTrace()
        Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
    }

    override fun changeDataSet(users: MutableList<User>) {
        adapter!!.changeDataSet(users)
    }

    override fun setAdapter(adapter: MemberAdapter) {
        Log.d(TAG_LOG, "set adapter")
        Log.d(TAG_LOG, "type adapter =  " + currentType!!)
        this.adapter = adapter
    }

    override fun loadRequests(currentId: String) {
        Log.d(TAG_LOG, "load requests")
        presenter!!.loadUsers(currentId, ADD_FRIEND)
    }

    override fun loadFriends(currentId: String) {
        Log.d(TAG_LOG, "load friends")
        presenter!!.loadUsers(currentId, REMOVE_FRIEND)

    }

    override fun loadReaders() {
        Log.d(TAG_LOG, "load readers")
        presenter!!.loadReaders()
    }

    override fun setProgressBar(progressBar: ProgressBar?) {
        this.progressBar = progressBar
    }

    override fun setNotLoading() {
        isLoading = false
    }

    override fun showLoading(disposable: Disposable) {
        progressBar!!.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar!!.visibility = View.GONE
    }

    override fun showDetails(item: User) {
        PersonalActivity.start(this, item)
    }

    override fun loadNextElements(i: Int) {
        presenter!!.loadNextElements(i)
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
            val intent = Intent(context, ReaderListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }
}
