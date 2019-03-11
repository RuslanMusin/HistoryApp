package com.summer.itis.cardsproject.ui.game.game_list.fragment

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
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.game.Lobby
import com.summer.itis.cardsproject.ui.game.add_game.AddGameActivity
import com.summer.itis.cardsproject.ui.game.game_list.GameAdapter
import com.summer.itis.cardsproject.ui.game.game_list.game.GameListView
import com.summer.itis.cardsproject.ui.widget.EmptyStateRecyclerView
import com.summer.itis.cardsproject.utils.AppHelper
import com.summer.itis.cardsproject.utils.Const
import kotlinx.android.synthetic.main.fragment_test_list.*
import java.util.ArrayList

class GameListFragment : Fragment() {

    private var progressBar: ProgressBar? = null
    private var recyclerView: EmptyStateRecyclerView? = null
    private var tvEmpty: TextView? = null

    private val isLoading = false

    lateinit var adapter: GameAdapter

    private var type: String? = null

    private var parentView: GameListView? = null

    private var isLoaded = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_test_list, container, false)
        Log.d(Const.TAG_LOG, "create view = " + this.type!!)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(Const.TAG_LOG, "on view created = " + this.type!!)
        initViews(view)
        initRecycler()

        if (!isLoaded && type.equals(Const.OFFICIAL_LIST)) {
//            parentView!!.changeAdapter(0)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initViews(view: View) {
        progressBar = view.findViewById(R.id.pg_comics_list)
        recyclerView = view.findViewById(R.id.rv_comics_list)
        tvEmpty = view.findViewById(R.id.tv_empty)
    }

    private fun initRecycler() {
        adapter = GameAdapter(ArrayList<Lobby>())
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
                Log.d(Const.TAG_LOG, "act float btn")
                MaterialDialog.Builder(activity as Activity)
                        .title(R.string.create_new_game)
                        .content(R.string.old_game_will_be_deleted)
                        .positiveText("Создать")
                        .onPositive(object :MaterialDialog.SingleButtonCallback {
                            override fun onClick(dialog: MaterialDialog, which: DialogAction) {
                                AppHelper.currentUser?.let { it.lobbyId?.let { it1 ->
                                    adapter.removeItemById(it1)
                                } }
                                AddGameActivity.start(activity as Activity)
                            }

                        })
                        .negativeText("Отмена")
                        .onNegative{ dialog, action -> dialog.cancel()}
                        .show()


            }
        })

    }


    fun loadPeople() {
        when (type) {

            Const.USER_LIST -> parentView!!.loadUserTests()

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

        fun newInstance(type: String, parentView: GameListView): Fragment {
            val fragment = GameListFragment()
            fragment.type = type
            fragment.parentView = parentView
            return fragment
        }
    }

}