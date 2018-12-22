package com.summer.itis.cardsproject.ui.tests.test_list.fragment

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView


import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.Test
import com.summer.itis.cardsproject.model.User
import com.summer.itis.cardsproject.repository.json.UserRepository
import com.summer.itis.cardsproject.ui.tests.add_test.AddTestActivity
import com.summer.itis.cardsproject.ui.tests.test_list.TestAdapter
import com.summer.itis.cardsproject.ui.tests.test_list.test.TestListView
import com.summer.itis.cardsproject.ui.widget.EmptyStateRecyclerView
import com.summer.itis.cardsproject.utils.Const.MY_LIST
import com.summer.itis.cardsproject.utils.Const.OFFICIAL_LIST
import com.summer.itis.cardsproject.utils.Const.READER_LIST
import com.summer.itis.cardsproject.utils.Const.TAG_LOG
import com.summer.itis.cardsproject.utils.Const.USER_LIST
import kotlinx.android.synthetic.main.fragment_test_list.*

import java.util.ArrayList


class TestListFragment : Fragment() {

    private var progressBar: ProgressBar? = null
    private var recyclerView: EmptyStateRecyclerView? = null
    private var tvEmpty: TextView? = null

    private val isLoading = false

    private var adapter: TestAdapter? = null

    private var type: String? = null

    private var parentView: TestListView? = null

    private var isLoaded = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_test_list, container, false)
        Log.d(TAG_LOG, "create view = " + this.type!!)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG_LOG, "on view created = " + this.type!!)
        initViews(view)
        initRecycler()

        if (!isLoaded && type.equals(OFFICIAL_LIST)) {
            parentView!!.changeAdapter(0)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initViews(view: View) {
        progressBar = view.findViewById(R.id.pg_comics_list)
        recyclerView = view.findViewById(R.id.rv_comics_list)
        tvEmpty = view.findViewById(R.id.tv_empty)
    }

    private fun initRecycler() {
        adapter = TestAdapter(ArrayList<Test>())
        val manager = LinearLayoutManager(this.activity)
        recyclerView!!.layoutManager = manager
        recyclerView!!.setEmptyView(tvEmpty!!)
        adapter!!.attachToRecyclerView(recyclerView!!)
        adapter!!.setOnItemClickListener(parentView)
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
                AddTestActivity.start(activity as Activity)
            }
        })

    }


    fun loadPeople() {
        when (type) {

            USER_LIST -> parentView!!.loadUserTests()

            MY_LIST -> parentView!!.loadMyTests(UserRepository.currentId)

            else -> parentView!!.loadOfficialTests()
        }
        isLoaded = true
    }

    fun changeDataInAdapter() {
        type?.let { parentView!!.setCurrentType(it) }
        adapter?.let { parentView!!.setAdapter(it) }
        parentView?.let { it.setProgressBar(progressBar) }
        loadPeople()
    }

    companion object {

        fun newInstance(type: String, parentView: TestListView): Fragment {
            val fragment = TestListFragment()
            /*   Bundle args = new Bundle();
        fragment.setArguments(args);*/
            fragment.type = type
            fragment.parentView = parentView
            return fragment
        }
    }

}

