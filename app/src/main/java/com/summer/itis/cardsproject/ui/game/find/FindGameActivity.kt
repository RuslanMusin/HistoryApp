package com.summer.itis.summerproject.ui.game.find

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.util.Log
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.summer.itis.summerproject.R
import com.summer.itis.summerproject.ui.base.EasyNavigationBaseActivity
import com.summer.itis.summerproject.ui.game.play.PlayGameActivity
import com.summer.itis.summerproject.utils.Const.TAG_LOG
import kotlinx.android.synthetic.main.layout_find_game.*

class FindGameActivity : EasyNavigationBaseActivity(), FindGameView {

    @InjectPresenter
    lateinit var presenter: FindGamePresenter

    override fun getContentLayout(): Int {
        return R.layout.activity_find_game
    }

    override fun gameFinded(gameMode: String) {
        PlayGameActivity.start(this, gameMode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        showNotSearching();

        btn_find_game.setOnClickListener {
            presenter.findGame()
        }

        btn_cancel.setOnClickListener {
            presenter.cancelSearching()
        }

        btn_find_bot.setOnClickListener{
            Log.d(TAG_LOG,"find bot")
//            presenter.findBotGame()
        }

    }

    override fun showNothing() {
        layout_searching.visibility = View.GONE
        btn_find_game.visibility = View.GONE
        tv_not_enough_cards.visibility = View.GONE

        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun showNotEnoughCards() {
        layout_searching.visibility = View.GONE
        btn_find_game.visibility = View.GONE
        tv_not_enough_cards.visibility = View.VISIBLE

        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun showNotSearching() {
        layout_searching.visibility = View.GONE
        btn_find_game.visibility = View.VISIBLE
        tv_not_enough_cards.visibility = View.GONE

        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun showSearching() {
        layout_searching.visibility = View.VISIBLE
        btn_find_game.visibility = View.GONE
        tv_not_enough_cards.visibility = View.GONE

        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, FindGameActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }
}
