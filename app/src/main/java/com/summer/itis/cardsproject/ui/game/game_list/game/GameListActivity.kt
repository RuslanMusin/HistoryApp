package com.summer.itis.cardsproject.ui.game.game_list.game

import android.app.ProgressDialog
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
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.game.Lobby
import com.summer.itis.cardsproject.repository.json.UserRepository
import com.summer.itis.cardsproject.ui.base.NavigationBaseActivity
import com.summer.itis.cardsproject.ui.game.bot_play.BotGameActivity
import com.summer.itis.cardsproject.ui.game.game_list.GameAdapter
import com.summer.itis.cardsproject.ui.game.game_list.fragment.GameListFragment
import com.summer.itis.cardsproject.ui.game.play.PlayGameActivity
import com.summer.itis.cardsproject.ui.service.GameService
import com.summer.itis.cardsproject.utils.Const
import com.summer.itis.cardsproject.utils.Const.ONLINE_STATUS
import com.summer.itis.cardsproject.utils.Const.TAG_LOG
import io.reactivex.disposables.Disposable

class GameListActivity : NavigationBaseActivity(), GameListView {

    private var toolbar: Toolbar? = null
    private var progressBar: ProgressBar? = null
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    private var adapter: GameAdapter? = null

    @InjectPresenter
    lateinit var presenter: GameListPresenter

    private var isLoading = false
    private var currentType: String? = null

    var mProgressDialog: ProgressDialog? = null

    var isClickable: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        setStatus(ONLINE_STATUS)
        waitEnemy()
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.container)
        layoutInflater.inflate(R.layout.activity_test_pager, contentFrameLayout)

        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar?.title = getString(R.string.games)
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
                this@GameListActivity.changeAdapter(tab.position)
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
        adapter.addFragment(GameListFragment.newInstance(Const.OFFICIAL_LIST, this), Const.OFFICIAL_LIST)
        adapter.addFragment(GameListFragment.newInstance(Const.USER_LIST, this), Const.USER_LIST)
        this.currentType = Const.OFFICIAL_LIST
        viewPager.adapter = adapter
    }

    /*override fun waitEnemy() {
        startService(
                Intent(this, GameService::class.java))
    }*/

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.game_list_menu, menu)

        val botItem = menu.findItem(R.id.action_find_bot)
        botItem.setOnMenuItemClickListener{
            Log.d(TAG_LOG,"find bot")
            presenter.findBotGame()
            true
        }

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
                        Const.OFFICIAL_LIST -> presenter!!.loadOfficialTestsByQUery(query)

                        Const.USER_LIST -> presenter!!.loadUserTestsByQUery(query, UserRepository.currentId)
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

        fun getFragmentForChange(position: Int): GameListFragment {
            return mFragmentList[position] as GameListFragment
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

    override fun onItemClick(item: Lobby) {
        if(isClickable) {
            presenter!!.onItemClick(item)
            isClickable = false
        }
    }

    override fun showSnackbar(msg: String) {
        super.showSnackBar(msg)
    }

    override fun handleError(error: Throwable) {
        Log.d(Const.TAG_LOG, "error = " + error.message)
        error.printStackTrace()
        Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
    }

    override fun changeDataSet(users: List<Lobby>) {
        adapter!!.changeDataSet(users)
    }

    override fun setAdapter(adapter: GameAdapter) {
        Log.d(Const.TAG_LOG, "set adapter")
        Log.d(Const.TAG_LOG, "type adapter =  " + currentType!!)
        this.adapter = adapter
    }

    override fun loadOfficialTests() {
        Log.d(Const.TAG_LOG, "load requests")
        presenter!!.loadOfficialTests()
    }

    override fun loadUserTests() {
        Log.d(Const.TAG_LOG, "load friends")
        presenter!!.loadUserTests(UserRepository.currentId)
    }




    override fun setProgressBar(progressBar: ProgressBar?) {
        this.progressBar = progressBar
        Log.d(Const.TAG_LOG,"set proggress  and type = $currentType}")
    }

    override fun setNotLoading() {
        isLoading = false
    }

    override fun showLoading(disposable: Disposable) {
        Log.d(Const.TAG_LOG,"show loading  and type = $currentType}")
        progressBar!!.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        Log.d(Const.TAG_LOG,"hide loading and type = $currentType}")
        progressBar!!.visibility = View.GONE
    }

    override fun showDetails(lobby: Lobby) {
        Log.d(Const.TAG_LOG,"show test act")
        presenter.findGame(lobby)

    }

    override fun onGameFinded(){
            Log.d(TAG_LOG, "start usual game")
//            hideProgressDialog()
            PlayGameActivity.start(this)
    }


    override fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog?.let {
                it.setMessage(getString(R.string.loading))
                it.isIndeterminate = true
                it.setCancelable(false)
            }
        }

        mProgressDialog!!.show()
    }

    override fun hideProgressDialog() {
        showSnackBar("Противник не принял приглашение")
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
        isClickable = true
    }

    override fun onBotGameFinded() {
        Log.d(TAG_LOG,"start bot")
        BotGameActivity.start(this)
    }

    override fun loadNextElements(i: Int) {
//        presenter!!.loadNextElements(i)
    }

    override fun setCurrentType(type: String) {
        Log.d(Const.TAG_LOG, "current type = $type")
        this.currentType = type
    }

    fun getCurrentType(): String? {
        return currentType
    }

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, GameListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }
}
