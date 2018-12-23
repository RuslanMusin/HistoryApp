package com.summer.itis.cardsproject.ui.cards.add_card_list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView

import com.annimon.stream.Stream
import com.arellomobile.mvp.presenter.InjectPresenter
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.Card
import com.summer.itis.cardsproject.model.pojo.opensearch.Item
import com.summer.itis.cardsproject.ui.base.BaseAdapter
import com.summer.itis.cardsproject.ui.base.NavigationBaseActivity
import com.summer.itis.cardsproject.ui.cards.add_card.AddCardActivity
import com.summer.itis.cardsproject.ui.cards.add_card.AddCardActivity.Companion.ITEM_JSON
import com.summer.itis.cardsproject.ui.tests.add_test.AddTestActivity
import com.summer.itis.cardsproject.ui.widget.EmptyStateRecyclerView
import com.summer.itis.cardsproject.utils.AppHelper
import com.summer.itis.cardsproject.utils.Const.EDIT_STATUS

import java.util.ArrayList
import java.util.regex.Pattern

import com.summer.itis.cardsproject.utils.Const.TAG_LOG
import com.summer.itis.cardsproject.utils.Const.gsonConverter
import io.reactivex.disposables.Disposable

class AddCardListActivity : NavigationBaseActivity(), AddCardListView, BaseAdapter.OnItemClickListener<Item> {

    private var card: Card? = null

    private var toolbar: Toolbar? = null

    @InjectPresenter
    lateinit var listPresenter: AddCardListPresenter

    private var recyclerView: EmptyStateRecyclerView? = null
    private var tvEmpty: TextView? = null
    private lateinit var progressBar: ProgressBar


    private var adapter: AddCardListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatus(EDIT_STATUS)
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_add_list)

            card = Card()

            initViews()
            initRecycler()

    }

    private fun initViews() {
        findViews()
        setSupportActionBar(toolbar)
        setBackArrow(toolbar!!)
        setToolbarTitle("Поиск личности")

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

    override fun setOpenSearchList(list: List<Item>) {
        var itemList = list

        Log.d(TAG_LOG,"setResult = " + itemList.size)

       /* for (item in itemList) {
            Log.d(TAG_LOG, "text " + item.text!!.content!!)
            Log.d(TAG_LOG, "desc " + item.description!!.content!!)
            Log.d(TAG_LOG, "url " + item.url!!.content!!)
        }*/
        val sep = "-----------"
        Log.d(TAG_LOG, sep)

        val names: List<String> = AppHelper.readFileFromAssets("regular.txt",this)
        for(name in names) {
            Log.d(TAG_LOG,"name = " + name)
        }
        itemList = Stream.of(itemList)
                .filter { e ->
                    var flag: Boolean = false
                    e.description?.let {
                        flag = true
                        val text = it.content
                        Log.d(TAG_LOG, "text = " + text)
//                    val pattern = Pattern.compile(".*\\(.*[0-9]{1,4}.*(\\s*-\\s*[0-9]{1,4}.*)?\\).*")
                        val mainPattern = Pattern.compile(".*\\(.*(([0-9]{1,4})|(век|др\\.)).*\\).*")
                        val secondPattern = Pattern.compile("\\(.*\\)\\s*—")
                        val thirdPattern = Pattern.compile("\\s+|,|\\.")
                        flag = mainPattern.matcher(text!!).matches()
                        if (flag) {
                            Log.d(TAG_LOG, "text true = " + text)
                            val partsOne = text.split(secondPattern)
                            val parts: MutableList<String> = ArrayList()
                            for(part in partsOne) {
                                Log.d(TAG_LOG,"big_part = $part")
                                val partsMin: List<String> = part.split(thirdPattern)
                                for(partMin in partsMin) {
                                    Log.d(TAG_LOG,"partMin = $partMin")
                                }
                                parts.addAll(partsMin)
                            }
                           /* val partOne = parts[0]
                            var partTwo = ""
                            if (parts.size > 1) {
                                partTwo = parts[1]
                            }
                            Log.d(TAG_LOG, "part = " + partOne)
                            Log.d(TAG_LOG, "partTwo = " + partTwo)*/
                            for (name in names) {
                                for(part in parts) {
                                    if (part.equals(name)) {
                                        flag = false
                                        Log.d(TAG_LOG, "flag = $flag and name = $name")
                                        break
                                    }
                                }
                            }
                        }
                    }

                    flag
                }
                .toList()
        for (item in itemList) {
          /*  Log.d(TAG_LOG, "text " + item.text!!.content!!)
            Log.d(TAG_LOG, "desc " + item.description!!.content!!)
            Log.d(TAG_LOG, "url " + item.url!!.content!!)*/
        }

        if(itemList.size == 0) {
            showSnackBar("Ничего не найдено")
        } else {
            adapter!!.changeDataSet(itemList)
        }


    }

    override fun onBackPressed() {
        val intent = Intent()
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)

        var searchView: android.support.v7.widget.SearchView? = null
        if (searchItem != null) {
            searchView = searchItem.actionView as android.support.v7.widget.SearchView
        }
        if (searchView != null) {
            val finalSearchView = searchView
            searchView.setOnQueryTextListener(object : android.support.v7.widget.SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.d(TAG_LOG,"opensearch")
                    if(checkSearch(query)) {
                        listPresenter.opensearch(query)
                        if (!finalSearchView.isIconified) {
                            finalSearchView.isIconified = true
                        }
                        searchItem!!.collapseActionView()
                    } else {
                        showSnackBar("Поиск возможен только на русском с использованием цифр и тире")
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    return false
                }
            })
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun checkSearch(query: String): Boolean {
        val pattern:Pattern = Pattern.compile("[А-я0-9_\\-,.]+")
        return (pattern.matcher(query).matches())
    }

    private fun initRecycler() {
        adapter = AddCardListAdapter(ArrayList())
        val manager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = manager
        recyclerView!!.setEmptyView(tvEmpty!!)
        adapter!!.attachToRecyclerView(recyclerView!!)
        adapter!!.setOnItemClickListener(this)
        recyclerView!!.adapter = adapter
        recyclerView!!.setHasFixedSize(true)
    }


    override fun onItemClick(item: Item) {
        val intent = Intent(this, AddCardActivity::class.java)
        val itemJson = gsonConverter.toJson(item)
        intent.putExtra(ITEM_JSON, itemJson)
        startActivityForResult(intent, ADD_CARD)
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)

        if (reqCode == ADD_CARD && resultCode == Activity.RESULT_OK) {
            val card = data?.getStringExtra(CARD_EXTRA)
            val intent = Intent()
            intent.putExtra(CARD_EXTRA, card)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else {
            onBackPressed()
        }
    }

    companion object {

        val CARD_EXTRA = "card"

        private val ADD_CARD = 1

        fun start(activity: Activity) {
            val intent = Intent(activity, AddTestActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
