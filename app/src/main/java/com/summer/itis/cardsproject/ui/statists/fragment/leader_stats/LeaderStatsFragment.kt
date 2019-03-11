package com.summer.itis.cardsproject.ui.statists.fragment.leader_stats

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.LeaderStat
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_recycler_list.*

class LeaderStatsFragment : MvpAppCompatFragment(), LeaderStatsView {

    private lateinit var adapter: LeaderStatsAdapter

    lateinit var themes: MutableList<LeaderStat>
    lateinit var userId: String

    @InjectPresenter
    lateinit var presenter: LeaderStatsPresenter

    companion object {

        fun newInstance(args: Bundle): Fragment {
            val fragment = LeaderStatsFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): Fragment {
            val fragment = LeaderStatsFragment()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_stats, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        presenter.loadStats()
    }

    override fun showStats(themes: List<LeaderStat>) {
        this.themes = themes.toMutableList()
        changeDataSet(this.themes)
    }

    private fun initViews() {
        initRecycler()
        setListeners()
    }

    private fun setListeners() {
    }

    override fun setNotLoading() {

    }

    override fun showLoading(disposable: Disposable) {
        pg_comics_list.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        pg_comics_list.visibility = View.GONE
    }

    override fun loadNextElements(i: Int) {
    }


    override fun changeDataSet(tests: List<LeaderStat>) {
        adapter.changeDataSet(tests)
        hideLoading()
    }

    override fun handleError(throwable: Throwable) {

    }

    private fun initRecycler() {
        adapter = LeaderStatsAdapter(ArrayList())
        val manager = LinearLayoutManager(activity as Activity)
        rv_comics_list.layoutManager = manager
        rv_comics_list.setEmptyView(tv_empty)
        adapter.attachToRecyclerView(rv_comics_list)
        adapter.setOnItemClickListener(this)
    }

    override fun onItemClick(item: LeaderStat) {

    }
}
