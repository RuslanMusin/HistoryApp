package com.summer.itis.cardsproject.ui.member.member_list.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView


import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.User
import com.summer.itis.cardsproject.repository.json.UserRepository
import com.summer.itis.cardsproject.ui.member.member_list.MemberAdapter
import com.summer.itis.cardsproject.ui.member.member_list.reader.ReaderListView
import com.summer.itis.cardsproject.ui.widget.EmptyStateRecyclerView
import com.summer.itis.cardsproject.utils.Const.FRIEND_LIST
import com.summer.itis.cardsproject.utils.Const.READER_LIST
import com.summer.itis.cardsproject.utils.Const.REQUEST_LIST
import com.summer.itis.cardsproject.utils.Const.TAG_LOG

import java.util.ArrayList


class ReaderListFragment : Fragment() {

    private var progressBar: ProgressBar? = null
    private var recyclerView: EmptyStateRecyclerView? = null
    private var tvEmpty: TextView? = null

    private val isLoading = false

    private var adapter: MemberAdapter? = null

    private var type: String? = null

    private var parentView: ReaderListView? = null

    private var isLoaded = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recycler_list, container, false)
        Log.d(TAG_LOG, "create view = " + this.type!!)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG_LOG, "on view created = " + this.type!!)
        initViews(view)
        initRecycler()

        if (!isLoaded && type == READER_LIST) {
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
        adapter = MemberAdapter(ArrayList<User>())
        val manager = LinearLayoutManager(this.activity)
        recyclerView!!.layoutManager = manager
        recyclerView!!.setEmptyView(tvEmpty!!)
        adapter!!.attachToRecyclerView(recyclerView!!)
        adapter!!.setOnItemClickListener(parentView)
        recyclerView!!.adapter = adapter
    }

    fun loadPeople() {
        when (type) {

            FRIEND_LIST -> parentView!!.loadFriends(UserRepository.currentId)

            REQUEST_LIST -> parentView!!.loadRequests(UserRepository.currentId)

            else -> parentView!!.loadReaders()
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

        fun newInstance(type: String, parentView: ReaderListView): Fragment {
            val fragment = ReaderListFragment()
            /*   Bundle args = new Bundle();
        fragment.setArguments(args);*/
            fragment.type = type
            fragment.parentView = parentView
            return fragment
        }
    }

}
