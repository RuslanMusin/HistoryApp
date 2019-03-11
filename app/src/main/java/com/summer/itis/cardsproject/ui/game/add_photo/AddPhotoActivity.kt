package com.summer.itis.cardsproject.ui.game.add_photo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.db_dop_models.PhotoItem
import com.summer.itis.cardsproject.ui.base.BaseAdapter
import com.summer.itis.cardsproject.ui.base.NavigationBaseActivity
import com.summer.itis.cardsproject.ui.cards.add_card.AddCardActivity.Companion.ITEM_JSON
import com.summer.itis.cardsproject.ui.widget.EmptyStateRecyclerView
import com.summer.itis.cardsproject.utils.Const.EDIT_STATUS
import com.summer.itis.cardsproject.utils.Const.USER_ID
import com.summer.itis.cardsproject.utils.Const.gsonConverter
import io.reactivex.disposables.Disposable

class AddPhotoActivity : NavigationBaseActivity(), AddPhotoView, BaseAdapter.OnItemClickListener<PhotoItem> {

    private var toolbar: Toolbar? = null

    @InjectPresenter
    lateinit var listPresenter: AddPhotoPresenter

    private var recyclerView: EmptyStateRecyclerView? = null
    private var tvEmpty: TextView? = null
    private lateinit var progressBar: ProgressBar

    private var listAdapter: AddPhotoListAdapter? = null

    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatus(EDIT_STATUS)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_list)

        userId = intent.getStringExtra(USER_ID)

        initViews()
        initRecycler()

        listPresenter.loadPhotos(userId)
    }

    private fun initViews() {
        findViews()
        setSupportActionBar(toolbar)
        setBackArrow(toolbar!!)
        setToolbarTitle("Выбор значка")

    }

    override fun handleError(throwable: Throwable) {

    }

    override fun showLoading(disposable: Disposable) {
        progressBar!!.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar!!.visibility = View.GONE
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
        listAdapter = AddPhotoListAdapter(ArrayList())
        val manager = GridLayoutManager(this,3)
        recyclerView!!.layoutManager = manager
        recyclerView!!.setEmptyView(tvEmpty!!)
        listAdapter!!.attachToRecyclerView(recyclerView!!)
        listAdapter!!.setOnItemClickListener(this)
        recyclerView!!.adapter = listAdapter
        recyclerView!!.setHasFixedSize(true)
    }

    override fun onItemClick(item: PhotoItem) {
        val intent = Intent()
        val itemJson = gsonConverter.toJson(item)
        intent.putExtra(ITEM_JSON, itemJson)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun changeDataSet(tests: List<PhotoItem>) {
        listAdapter!!.changeDataSet(tests)
    }
    companion object {

        fun start(activity: Activity) {
            val intent = Intent(activity, AddPhotoActivity::class.java)
            activity.startActivity(intent)
        }
    }
}