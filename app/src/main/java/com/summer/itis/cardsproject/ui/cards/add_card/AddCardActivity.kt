package com.summer.itis.cardsproject.ui.cards.add_card

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.TextView

import com.arellomobile.mvp.presenter.InjectPresenter
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.R.id.*
import com.summer.itis.cardsproject.R.string.card
import com.summer.itis.cardsproject.model.Card
import com.summer.itis.cardsproject.model.pojo.opensearch.Item
import com.summer.itis.cardsproject.model.pojo.query.Page
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.userRepository
import com.summer.itis.cardsproject.ui.base.BaseActivity
import com.summer.itis.cardsproject.ui.base.NavigationBaseActivity
import com.summer.itis.cardsproject.ui.cards.add_card_list.AddCardListActivity.Companion.CARD_EXTRA
import com.summer.itis.cardsproject.ui.tests.add_test.AddTestActivity
import com.summer.itis.cardsproject.utils.Const
import com.summer.itis.cardsproject.utils.Const.EDIT_STATUS
import com.summer.itis.cardsproject.utils.Const.TAG_LOG

import com.summer.itis.cardsproject.utils.Const.gsonConverter
import kotlinx.android.synthetic.main.layout_add_card.*

class AddCardActivity : NavigationBaseActivity(), AddCardView, SeekBar.OnSeekBarChangeListener {

    private var card: Card? = null

    private var toolbar: Toolbar? = null

    private lateinit var seekBars: List<SeekBar>
    var seekChanged:SeekBar? = null
    private lateinit var seeksChanges: MutableList<SeekBar>
    private var numberSeek: Int = 0

    private var balance: Int = 50

    @InjectPresenter
    lateinit var presenter: AddCardPresenter

    private var item: Item? = null

    private var isBalanced: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatus(EDIT_STATUS)

            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_add_card)

            card = Card()
            seekBars = listOf<SeekBar>(seekBarSupport, seekBarIntelligence, seekBarPrestige, seekBarHp, seekBarStrength)
            seeksChanges = ArrayList()
            item = gsonConverter.fromJson(intent.getStringExtra(ITEM_JSON), Item::class.java)
            card?.abstractCard?.wikiUrl = item!!.url!!.content
            card?.abstractCard?.description = item!!.description!!.content
            item!!.text!!.content?.let { presenter.query(it) }
            setBalance()
            initViews()
            setListeners()

    }

    fun setBalance() {
        var newBalance = 0
        for(seek in seekBars) {
            newBalance += seek.progress
        }
        balance = newBalance
        Log.d(TAG_LOG,"set balance = $balance")
    }

    private fun initViews() {
        findViews()
        //        supportActionBar(toolbar);
        setSupportActionBar(toolbar)
        setBackArrow(toolbar!!)
        setToolbarTitle("Статистика карты")

    }

    override fun onBackPressed() {
        val intent = Intent()
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }

    private fun setListeners() {
        seekBarStrength!!.setOnSeekBarChangeListener(this)
        seekBarHp!!.setOnSeekBarChangeListener(this)
        seekBarPrestige!!.setOnSeekBarChangeListener(this)
        seekBarIntelligence!!.setOnSeekBarChangeListener(this)
        seekBarSupport!!.setOnSeekBarChangeListener(this)

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        changeTvSeekbar(seekBar)
//        Log.d(TAG_LOG,"isBalanced = $isBalanced")
        if(fromUser) {
            Log.d(TAG_LOG, "from user")
            Log.d(TAG_LOG,"set balance")
            setBalance()
            Log.d(TAG_LOG,"balance with otheres")
            balanceWithOthers(seekBar)
        }
    }

    private fun changeTvSeekbar(seekBar: SeekBar?) {
        val strProgress: String = seekBar?.progress.toString()
        when (seekBar?.id) {
            R.id.seekBarHp -> {
                tvHp!!.setText(strProgress)
                Log.d(TAG_LOG,"seek changed = hp" )
            }

            R.id.seekBarPrestige -> {
                tvPrestige!!.setText(strProgress)
                Log.d(TAG_LOG,"seek changed = prestige" )
            }

            R.id.seekBarIntelligence -> {
                tvIntelligence!!.setText(strProgress)
                Log.d(TAG_LOG,"seek changed = intel" )
            }

            R.id.seekBarSupport -> {
                tvSupport!!.setText(strProgress)
                Log.d(TAG_LOG,"seek changed = support" )
            }

            R.id.seekBarStrength -> {
                tvStrength!!.setText(strProgress)
                Log.d(TAG_LOG,"seek changed = strenght" )
            }
        }
    }

    private fun balanceWithOthers(seekBar: SeekBar?) {
        if(seekChanged == null || seekBar?.id != seekChanged?.id) {
            seeksChanges = ArrayList()
            numberSeek = 0
            for(seek in seekBars) {
                if (!(seek.id == seekBar?.id)){
                    seeksChanges.add(seek)
                }
            }
            Log.d(TAG_LOG,"seekChanged  = " + seekChanged)
        }
        seekChanged = seekBar
        while(balance != 50) {
            for (numb in seeksChanges.indices) {
                val changeSeek = seeksChanges[numberSeek]
                numberSeek = if(numberSeek != (seeksChanges.size-1)) (numberSeek+1) else 0
//                Log.d(TAG_LOG,"numberSeek = " + numberSeek)
                if(balance == 50) {
                    return
                }
                if(balance > 50 && changeSeek.progress > 0) {
                    changeSeek.progress--
                    balance--
                } else if(balance < 50) {
                    changeSeek.progress++
                    balance++
                }
                Log.d(TAG_LOG,"balance = " + balance)
            }

        }
    }


    override fun setQueryResults(list: List<Page>) {
        val page = list[0]
        card!!.abstractCard?.name = page.title
        card!!.abstractCard?.photoUrl = page.original!!.source
        card!!.abstractCard?.extract = page.extract!!.content

    }

    override fun handleError(throwable: Throwable) {

    }

    private fun findViews() {
        toolbar = findViewById(R.id.toolbar)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)

        val checkItem = menu.findItem(R.id.action_check)
        checkItem.setOnMenuItemClickListener {
            prepareCard()
            val intent = Intent()
            Log.d(TAG_LOG,"wiki url = " + card?.abstractCard?.wikiUrl)
            val cardJson = gsonConverter.toJson(card)
            intent.putExtra(CARD_EXTRA, cardJson)
            setResult(Activity.RESULT_OK, intent)
            finish()

            true
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun prepareCard() {
        card?.hp = seekBarHp.progress
        card?.strength = seekBarStrength.progress
        card?.support = seekBarSupport.progress
        card?.prestige = seekBarPrestige.progress
        card?.intelligence = seekBarIntelligence.progress
    }

    /* override fun onClick(view: View) {

         }
     }*/

    companion object {

        val CARD_EXTRA = "card"

        var ITEM_JSON = "item_json"

        fun start(activity: Activity, item: Item) {
            val intent = Intent(activity, AddTestActivity::class.java)
            val itemJson = gsonConverter.toJson(item)
            intent.putExtra(ITEM_JSON, itemJson)
            activity.startActivity(intent)
        }
    }


}
