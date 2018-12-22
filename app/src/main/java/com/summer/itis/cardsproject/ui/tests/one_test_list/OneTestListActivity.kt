package com.summer.itis.cardsproject.ui.tests.one_test_list

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
import com.summer.itis.cardsproject.model.Test
import com.summer.itis.cardsproject.ui.base.BaseAdapter
import com.summer.itis.cardsproject.ui.base.NavigationBaseActivity
import com.summer.itis.cardsproject.ui.tests.test_item.TestActivity
import com.summer.itis.cardsproject.ui.tests.test_list.TestAdapter
import com.summer.itis.cardsproject.ui.widget.EmptyStateRecyclerView
import com.summer.itis.cardsproject.utils.Const.ABSTRACT_CARD_ID
import com.summer.itis.cardsproject.utils.Const.DEFAULT_ABSTRACT_TESTS
import com.summer.itis.cardsproject.utils.Const.ONLINE_STATUS
import com.summer.itis.cardsproject.utils.Const.TEST_LIST_TYPE
import com.summer.itis.cardsproject.utils.Const.USER_ABSTRACT_TESTS
import com.summer.itis.cardsproject.utils.Const.USER_ID
import com.summer.itis.cardsproject.utils.Const.USER_TESTS
import io.reactivex.disposables.Disposable

class OneTestListActivity : NavigationBaseActivity(), OneTestListView, BaseAdapter.OnItemClickListener<Test> {

    private lateinit var toolbar: Toolbar

    @InjectPresenter
    lateinit var listPresenter: OneTestListPresenter

    private var recyclerView: EmptyStateRecyclerView? = null
    private var tvEmpty: TextView? = null
    private lateinit var progressBar: ProgressBar

    private var adapter: TestAdapter? = null

    private lateinit var type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatus(ONLINE_STATUS)
        waitEnemy()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_list)

        type = intent.getStringExtra(TEST_LIST_TYPE)

        initViews()
        initRecycler()

        loadTests()
    }

    private fun loadTests() {

        when {

            type.equals(DEFAULT_ABSTRACT_TESTS) -> {
                val abstractCardId: String = intent.getStringExtra(ABSTRACT_CARD_ID)
                listPresenter.loadDefaultAbstractCardTests(abstractCardId)
            }

            type.equals(USER_ABSTRACT_TESTS) -> {
                val abstractCardId: String = intent.getStringExtra(ABSTRACT_CARD_ID)
                val userId: String = intent.getStringExtra(USER_ID)
                listPresenter.loadMyAbstractCardTests(abstractCardId,userId)
            }

            type.equals(USER_TESTS) -> {
                val userId: String = intent.getStringExtra(USER_ID)
                listPresenter.loadUserTests(userId)
            }
        }
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


    override fun changeDataSet(tests: List<Test>) {
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
        adapter = TestAdapter(ArrayList())
        val manager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = manager
        recyclerView!!.setEmptyView(tvEmpty!!)
        adapter!!.attachToRecyclerView(recyclerView!!)
        adapter!!.setOnItemClickListener(this)
        recyclerView!!.adapter = adapter
    }


    override fun onItemClick(item: Test) {
        TestActivity.start(this, item)
    }

    companion object {

        fun start(activity: Activity, intent: Intent) {
            activity.startActivity(intent)
        }
    }
}