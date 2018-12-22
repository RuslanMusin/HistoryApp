package com.summer.itis.summerproject.ui.cards.cards_list.tabs

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.*
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.summer.itis.summerproject.R
import com.summer.itis.summerproject.model.AbstractCard
import com.summer.itis.summerproject.repository.json.UserRepository
import com.summer.itis.summerproject.ui.base.BaseAdapter
import com.summer.itis.summerproject.ui.cards.cards_list.CardsPresenter
import com.summer.itis.summerproject.ui.cards.cards_list.CardsView
import com.summer.itis.summerproject.ui.cards.cards_list.adapter.CardsListAdapter
import com.summer.itis.summerproject.utils.Const.ALL_LIST_CARDS
import com.summer.itis.summerproject.utils.Const.USER_LIST_CARDS
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import java.util.ArrayList

/**
 * Created by Home on 11.07.2018.
 */
abstract class AbstractCardsTabFragment : MvpAppCompatFragment(), CardsView, BaseAdapter.OnItemClickListener<AbstractCard>{

    protected lateinit var mRecyclerView: RecyclerView
    protected lateinit var mRecyclerViewAdapter: CardsListAdapter
    protected lateinit var cards: ArrayList<AbstractCard>
    @InjectPresenter
    lateinit var cardsPresenter : CardsPresenter

    lateinit var type: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recycler_list, container, false)
        mRecyclerView = view.findViewById(R.id.rv_comics_list)
        mRecyclerViewAdapter = CardsListAdapter(ArrayList<AbstractCard>())
        mRecyclerView.adapter = mRecyclerViewAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mRecyclerViewAdapter.setOnItemClickListener(this)
        setHasOptionsMenu(true);
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
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
                    when (type) {
                        ALL_LIST_CARDS -> cardsPresenter!!.loadCardsByQuery(query, UserRepository.currentId)

                        USER_LIST_CARDS -> cardsPresenter!!.loadUserCardsByQuery(query, UserRepository.currentId)

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
        return super.onCreateOptionsMenu(menu,menuInflater)
    }

    override fun showLoading(disposable: Disposable) {
        pg_comics_list.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        pg_comics_list.visibility = View.GONE
    }
}