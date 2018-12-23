package com.summer.itis.cardsproject.ui.epoch

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.Epoch
import com.summer.itis.cardsproject.model.Test
import com.summer.itis.cardsproject.model.UserEpoch
import com.summer.itis.cardsproject.ui.base.BaseAdapter
import com.summer.itis.cardsproject.ui.base.NavigationBaseActivity
import com.summer.itis.cardsproject.ui.statists.fragment.game_stats.GameStatsAdapter
import com.summer.itis.cardsproject.ui.statists.fragment.game_stats.GameStatsPresenter
import com.summer.itis.cardsproject.ui.statists.fragment.game_stats.GameStatsView
import com.summer.itis.cardsproject.ui.tests.one_test_list.OneTestListPresenter
import com.summer.itis.cardsproject.ui.tests.one_test_list.OneTestListView
import com.summer.itis.cardsproject.ui.tests.test_item.TestActivity
import com.summer.itis.cardsproject.ui.tests.test_list.TestAdapter
import com.summer.itis.cardsproject.ui.widget.EmptyStateRecyclerView
import com.summer.itis.cardsproject.utils.Const
import com.summer.itis.cardsproject.utils.Const.EPOCH_KEY
import com.summer.itis.cardsproject.utils.Const.gsonConverter
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_recycler_list.*

class EpochListActivity : NavigationBaseActivity(), EpochListView {

    private lateinit var toolbar: Toolbar

    @InjectPresenter
    lateinit var listPresenter: EpochListPresenter

    private var recyclerView: EmptyStateRecyclerView? = null
    private var tvEmpty: TextView? = null
    private lateinit var progressBar: ProgressBar

    private var adapter: EpochAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatus(Const.ONLINE_STATUS)
        waitEnemy()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_list)
        initViews()
        initRecycler()
        listPresenter.loadEpoches()
    }

    private fun initViews() {
        findViews()
        setSupportActionBar(toolbar)
        setBackArrow(toolbar)
        setToolbarTitle(getString(R.string.tests))

    }

    override fun setNotLoading() {

    }

    override fun showLoading(disposable: Disposable) {
        progressBar!!.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar!!.visibility = View.GONE
    }

    override fun loadNextElements(i: Int) {
    }


    override fun changeDataSet(tests: List<Epoch>) {
        adapter!!.changeDataSet(tests)
    }

    override fun handleError(throwable: Throwable) {

    }

    private fun findViews() {
        toolbar = findViewById(R.id.tb_books_list)
        recyclerView = findViewById(R.id.rv_comics_list)
        tvEmpty = findViewById(R.id.tv_empty)
        progressBar = findViewById(R.id.pg_comics_list)
    }

    override fun onBackPressed() {
        val intent = Intent()
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }

    private fun initRecycler() {
        adapter = EpochAdapter(ArrayList())
        val manager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = manager
        recyclerView!!.setEmptyView(tvEmpty!!)
        adapter!!.attachToRecyclerView(recyclerView!!)
        adapter!!.setOnItemClickListener(this)
        recyclerView!!.adapter = adapter
    }


    override fun onItemClick(item: Epoch) {
        val intent = Intent()
        intent.putExtra(EPOCH_KEY, gsonConverter.toJson(item))
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    companion object {

        fun start(activity: Activity, intent: Intent) {
            activity.startActivity(intent)
        }
    }
}