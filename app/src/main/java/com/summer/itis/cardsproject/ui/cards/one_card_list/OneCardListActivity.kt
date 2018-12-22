package com.summer.itis.cardsproject.ui.cards.one_card_list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.AbstractCard
import com.summer.itis.cardsproject.model.Card
import com.summer.itis.cardsproject.ui.base.BaseAdapter
import com.summer.itis.cardsproject.ui.base.NavigationBaseActivity
import com.summer.itis.cardsproject.ui.cards.card_item.CardActivity
import com.summer.itis.cardsproject.ui.cards.cards_list.adapter.CardsListAdapter
import com.summer.itis.cardsproject.ui.widget.EmptyStateRecyclerView
import com.summer.itis.cardsproject.utils.Const
import com.summer.itis.cardsproject.utils.Const.DEFAULT_ABSTRACT_TESTS
import com.summer.itis.cardsproject.utils.Const.ONLINE_STATUS
import io.reactivex.disposables.Disposable

class OneCardListActivity : NavigationBaseActivity(), OneCardListView, BaseAdapter.OnItemClickListener<AbstractCard> {

    private lateinit var toolbar: Toolbar

    @InjectPresenter
    lateinit var listPresenter: OneCardListPresenter

    private var recyclerView: EmptyStateRecyclerView? = null
    private var tvEmpty: TextView? = null
    private lateinit var progressBar: ProgressBar

    private var adapter: CardsListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatus(ONLINE_STATUS)
        waitEnemy()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_list)

        initViews()
        initRecycler()

        loadCards()
    }

    private fun loadCards() {
        val userId: String = intent.getStringExtra(Const.USER_ID)
        listPresenter.loadUserCards(userId)
    }

    private fun initViews() {
        findViews()
        setSupportActionBar(toolbar)
        setBackArrow(toolbar)
        setToolbarTitle(getString(R.string.card))

    }

    override fun setNotLoading() {

    }

    override fun showLoading(disposable: Disposable) {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    override fun loadNextElements(i: Int) {
    }


    override fun changeDataSet(cards: List<Card>) {
        val abstractCards: MutableList<AbstractCard> = ArrayList()
        for(card in cards) {
            abstractCards.add(card.abstractCard)
        }
        adapter!!.changeDataSet(abstractCards)
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
        adapter = CardsListAdapter(ArrayList())
        val manager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = manager
        recyclerView!!.setEmptyView(tvEmpty!!)
        adapter!!.attachToRecyclerView(recyclerView!!)
        adapter!!.setOnItemClickListener(this)
        recyclerView!!.adapter = adapter
    }


    override fun onItemClick(item: AbstractCard) {
        CardActivity.start(this, item,DEFAULT_ABSTRACT_TESTS)
    }

    companion object {

        fun start(activity: Activity, intent: Intent) {
            activity.startActivity(intent)
        }
    }
}