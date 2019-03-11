package com.summer.itis.cardsproject.ui.tests.test_list.test

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.Test
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.userRepository
import com.summer.itis.cardsproject.repository.json.UserRepository
import com.summer.itis.cardsproject.repository.json.UserRepository.Companion.currentId
import com.summer.itis.cardsproject.ui.base.NavigationBaseActivity
import com.summer.itis.cardsproject.ui.tests.add_test.AddTestActivity
import com.summer.itis.cardsproject.ui.tests.test_item.TestActivity
import com.summer.itis.cardsproject.ui.tests.test_list.TestAdapter
import com.summer.itis.cardsproject.ui.tests.test_list.fragment.TestListFragment
import com.summer.itis.cardsproject.ui.widget.EmptyStateRecyclerView
import com.summer.itis.cardsproject.utils.Const.EDIT_STATUS
import com.summer.itis.cardsproject.utils.Const.MY_LIST
import com.summer.itis.cardsproject.utils.Const.OFFICIAL_LIST
import com.summer.itis.cardsproject.utils.Const.ONLINE_STATUS
import com.summer.itis.cardsproject.utils.Const.TAG_LOG
import com.summer.itis.cardsproject.utils.Const.USER_LIST
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_test_list.*
import java.util.ArrayList

class TestListActivity : NavigationBaseActivity(), TestListView {

    private var toolbar: Toolbar? = null
    private var progressBar: ProgressBar? = null
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    private var adapter: TestAdapter? = null

    private var recyclerView: EmptyStateRecyclerView? = null
    private var tvEmpty: TextView? = null

    @InjectPresenter
    lateinit var presenter: TestListPresenter

    private var isLoading = false
    private var currentType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatus(ONLINE_STATUS)
        waitEnemy()
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.container)
        layoutInflater.inflate(R.layout.activity_test_list, contentFrameLayout)

        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar?.title = getString(R.string.tests)
        supportActionBar(toolbar!!)

        initViews()
        initRecycler()
        presenter.loadOfficialTests()

    }

    private fun initViews() {
        progressBar = findViewById(R.id.pg_comics_list)
        recyclerView = findViewById(R.id.rv_comics_list)
        tvEmpty = findViewById(R.id.tv_empty)
    }

    private fun initRecycler() {
        adapter = TestAdapter(ArrayList<Test>())
        val manager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = manager
        recyclerView!!.setEmptyView(tvEmpty!!)
        adapter!!.attachToRecyclerView(recyclerView!!)
        adapter!!.setOnItemClickListener(this)
        recyclerView!!.adapter = adapter

        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && floating_button.getVisibility() == View.VISIBLE) {
                    floating_button.hide();
                } else if (dy < 0 && floating_button.getVisibility() != View.VISIBLE) {
                    floating_button.show();
                }
            }
        })

        floating_button.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                Log.d(TAG_LOG,"act float btn")
                AddTestActivity.start(this@TestListActivity)
            }
        })

    }

   /* override fun onCreateOptionsMenu(menu: Menu): Boolean {
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
    }*/

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
