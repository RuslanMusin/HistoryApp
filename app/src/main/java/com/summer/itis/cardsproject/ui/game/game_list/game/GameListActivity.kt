package com.summer.itis.cardsproject.ui.game.game_list.game

import android.app.Activity
import android.app.ProgressDialog
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
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.game.Lobby
import com.summer.itis.cardsproject.repository.json.UserRepository
import com.summer.itis.cardsproject.ui.base.NavigationBaseActivity
import com.summer.itis.cardsproject.ui.game.add_game.AddGameActivity
import com.summer.itis.cardsproject.ui.game.bot_play.BotGameActivity
import com.summer.itis.cardsproject.ui.game.game_list.GameAdapter
import com.summer.itis.cardsproject.ui.game.game_list.fragment.GameListFragment
import com.summer.itis.cardsproject.ui.game.play.PlayGameActivity
import com.summer.itis.cardsproject.ui.service.GameService
import com.summer.itis.cardsproject.ui.widget.EmptyStateRecyclerView
import com.summer.itis.cardsproject.utils.AppHelper
import com.summer.itis.cardsproject.utils.Const
import com.summer.itis.cardsproject.utils.Const.ONLINE_STATUS
import com.summer.itis.cardsproject.utils.Const.TAG_LOG
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_test_list.*
import java.util.ArrayList

class GameListActivity : NavigationBaseActivity(), GameListView {

    private var toolbar: Toolbar? = null
    private var progressBar: ProgressBar? = null
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    private var adapter: GameAdapter? = null

    @InjectPresenter
    lateinit var presenter: GameListPresenter

    private var isLoading = false
    private var currentType: String? = null

    var mProgressDialog: ProgressDialog? = null

    var isClickable: Boolean = true

    private var recyclerView: EmptyStateRecyclerView? = null
    private var tvEmpty: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        setStatus(ONLINE_STATUS)
        waitEnemy()
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.container)
        layoutInflater.inflate(R.layout.activity_game_list, contentFrameLayout)

        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar?.title = getString(R.string.games)
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
        adapter = GameAdapter(ArrayList<Lobby>())
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
                Log.d(Const.TAG_LOG, "act float btn")
                MaterialDialog.Builder(this@GameListActivity)
                    .title(R.string.create_new_game)
                    .content(R.string.old_game_will_be_deleted)
                    .positiveText("Создать")
                    .onPositive(object :MaterialDialog.SingleButtonCallback {
                        override fun onClick(dialog: MaterialDialog, which: DialogAction) {
                            AppHelper.currentUser?.let { it.lobbyId?.let { it1 ->
                                adapter?.removeItemById(it1)
                            } }
                            AddGameActivity.start(this@GameListActivity)
                        }

                    })
                    .negativeText("Отмена")
                    .onNegative{ dialog, action -> dialog.cancel()}
                    .show()


            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.game_list_menu, menu)

        val botItem = menu.findItem(R.id.update_list)
        botItem.setOnMenuItemClickListener {
            presenter.loadOfficialTests()
            true
        }
       /* botItem.setOnMenuItemClickListener{
            Log.d(TAG_LOG,"find bot")
            presenter.findBotGame()
            true
        }*/

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
                        Const.OFFICIAL_LIST -> presenter!!.loadOfficialTestsByQUery(query)

                        Const.USER_LIST -> presenter!!.loadUserTestsByQUery(query, UserRepository.currentId)
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
    }

    override fun onItemClick(item: Lobby) {
        if(isClickable) {
            presenter!!.onItemClick(item)
            isClickable = false
        }
    }

    override fun showSnackbar(msg: String) {
        super.showSnackBar(msg)
    }

    override fun handleError(error: Throwable) {
        Log.d(Const.TAG_LOG, "error = " + error.message)
        error.printStackTrace()
        Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
    }

    override fun changeDataSet(users: List<Lobby>) {
        adapter!!.changeDataSet(users)
    }

    override fun setAdapter(adapter: GameAdapter) {
        Log.d(Const.TAG_LOG, "set adapter")
        Log.d(Const.TAG_LOG, "type adapter =  " + currentType!!)
        this.adapter = adapter
    }

    override fun loadOfficialTests() {
        Log.d(Const.TAG_LOG, "load requests")
        presenter!!.loadOfficialTests()
    }

    override fun loadUserTests() {
        Log.d(Const.TAG_LOG, "load friends")
        presenter!!.loadUserTests(UserRepository.currentId)
    }


    override fun setProgressBar(progressBar: ProgressBar?) {
        this.progressBar = progressBar
        Log.d(Const.TAG_LOG,"set proggress  and type = $currentType}")
    }

    override fun setNotLoading() {
        isLoading = false
    }

    override fun showLoading(disposable: Disposable) {
        Log.d(Const.TAG_LOG,"show loading  and type = $currentType}")
        progressBar!!.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        Log.d(Const.TAG_LOG,"hide loading and type = $currentType}")
        progressBar!!.visibility = View.GONE
    }

    override fun showDetails(lobby: Lobby) {
        Log.d(Const.TAG_LOG,"show test act")
        presenter.findGame(lobby)

    }

    override fun onGameFinded(){
            Log.d(TAG_LOG, "start usual game")
//            hideProgressDialog()
            PlayGameActivity.start(this)
    }


    override fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog?.let {
                it.setMessage(getString(R.string.loading))
                it.isIndeterminate = true
                it.setCancelable(false)
            }
        }

        mProgressDialog!!.show()
    }

    override fun hideProgressDialog() {
        showSnackBar("Противник не принял приглашение")
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
        isClickable = true
    }

    override fun onBotGameFinded() {
        Log.d(TAG_LOG,"start bot")
        BotGameActivity.start(this)
    }

    override fun loadNextElements(i: Int) {
//        presenter!!.loadNextElements(i)
    }

    override fun setCurrentType(type: String) {
        Log.d(Const.TAG_LOG, "current type = $type")
        this.currentType = type
    }

    fun getCurrentType(): String? {
        return currentType
    }

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, GameListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }
}
