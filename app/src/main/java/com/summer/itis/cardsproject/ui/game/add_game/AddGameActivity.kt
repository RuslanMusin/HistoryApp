package com.summer.itis.summerproject.ui.game.add_game

import android.app.*
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.bumptech.glide.Glide
import com.summer.itis.summerproject.R
import com.summer.itis.summerproject.model.Card
import com.summer.itis.summerproject.model.db_dop_models.PhotoItem
import com.summer.itis.summerproject.model.game.Lobby
import com.summer.itis.summerproject.model.game.LobbyPlayerData
import com.summer.itis.summerproject.repository.json.UserRepository
import com.summer.itis.summerproject.ui.base.BaseBackActivity
import com.summer.itis.summerproject.ui.base.NavigationBaseActivity
import com.summer.itis.summerproject.ui.cards.add_card.AddCardActivity.Companion.ITEM_JSON
import com.summer.itis.summerproject.ui.cards.add_card_list.AddCardListActivity
import com.summer.itis.summerproject.ui.game.add_photo.AddPhotoActivity
import com.summer.itis.summerproject.ui.game.add_photo.AddPhotoView
import com.summer.itis.summerproject.ui.game.game_list.game.GameListActivity
import com.summer.itis.summerproject.ui.tests.add_test.AddTestActivity.Companion.ADD_QUESTION_FRAGMENT
import com.summer.itis.summerproject.ui.tests.add_test.AddTestPresenter
import com.summer.itis.summerproject.ui.tests.add_test.fragments.main.AddTestFragment.Companion.ADD_CARD
import com.summer.itis.summerproject.ui.tests.add_test.fragments.question.AddQuestionFragment
import com.summer.itis.summerproject.ui.tests.test_item.TestActivity.Companion.TEST_JSON
import com.summer.itis.summerproject.utils.Const.ONLINE_GAME
import com.summer.itis.summerproject.utils.Const.TAG_LOG
import com.summer.itis.summerproject.utils.Const.gsonConverter
import kotlinx.android.synthetic.main.activity_add_game.*
import kotlinx.android.synthetic.main.item_member.view.*
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.widget.SeekBar
import com.summer.itis.summerproject.R.id.*
import com.summer.itis.summerproject.repository.RepositoryProvider.Companion.cardRepository
import com.summer.itis.summerproject.repository.RepositoryProvider.Companion.userRepository
import com.summer.itis.summerproject.ui.game.play.PlayGameActivity
import com.summer.itis.summerproject.ui.service.GameService
import com.summer.itis.summerproject.utils.Const.EDIT_STATUS
import com.summer.itis.summerproject.utils.Const.OFFICIAL_TYPE
import com.summer.itis.summerproject.utils.Const.ONLINE_STATUS
import com.summer.itis.summerproject.utils.Const.USER_ID
import com.summer.itis.summerproject.utils.Const.USER_TYPE


class AddGameActivity : NavigationBaseActivity(), AddGameView, View.OnClickListener {

    internal var PLACE_PICKER_REQUEST = 1

    @InjectPresenter
    lateinit var presenter: AddGamePresenter

    lateinit var lobby: Lobby

    lateinit var types: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatus(EDIT_STATUS)
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_add_game)
            initViews()
            lobby = Lobby()

    }


    private fun initViews() {
        setSupportActionBar(toolbar)
        setBackArrow(toolbar!!)
        setToolbarTitle("Новая игра")
        types = listOf(getString(R.string.user_type), getString(R.string.official_type))
        spinner.setItems(types)
        btn_add_game_photo.setOnClickListener(this)
        btn_create_game.setOnClickListener(this)
        seekBarCards.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val strProgress: String = seekBar?.progress.toString()
                tvCards.text = strProgress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.btn_create_game -> {
                lobby.cardNumber = seekBarCards.progress
                if(lobby.cardNumber >= 5) {
                    if (types[spinner.selectedIndex].equals(getString(R.string.official_type))) {
                        lobby.type = OFFICIAL_TYPE
                    } else {
                        lobby.type = USER_TYPE
                    }
                    Log.d(TAG_LOG,"lobby type = ${lobby.type}")
                    cardRepository.findCardsByType(UserRepository.currentId,lobby.type).subscribe { myCards ->
                        val mySize = myCards.size
                        Log.d(TAG_LOG,"mySize = $mySize and cardNumber = ${lobby.cardNumber}")
                        if (mySize >= lobby.cardNumber) {
                            lobby.title = et_game_name.text.toString()
                            lobby.lowerTitle = lobby.title?.toLowerCase()
                            lobby.status = ONLINE_STATUS
                            lobby.isFastGame = false
                            val playerData = LobbyPlayerData()
                            playerData.playerId = UserRepository.currentId
                            playerData.online = true
                            lobby.creator = playerData

                            presenter.createGame(lobby)
                        } else {
                            showSnackBar(R.string.you_dont_have_card_min)
                        }
                    }
                } else {
                    showSnackBar(R.string.set_card_min)
                }

            }

            R.id.btn_add_game_photo -> {
                val intent = Intent(this, AddPhotoActivity::class.java)
                intent.putExtra(USER_ID,UserRepository.currentId)
                startActivityForResult(intent, ADD_CARD)

            }
        }
    }



    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)

        if (reqCode == ADD_CARD && resultCode == Activity.RESULT_OK) {
            val photoItem = gsonConverter.fromJson(data!!.getStringExtra(ITEM_JSON), PhotoItem::class.java)
            Glide.with(iv_cover.context)
                    .load(photoItem.photoUrl)
                    .into(iv_cover)
            lobby.photoUrl = photoItem.photoUrl
        }
    }

    override fun onGameCreated() {
       /* startService(
                Intent(this, GameService::class.java))*/
        GameListActivity.start(this)
    }


    companion object {

        fun start(activity: Activity) {
            val intent = Intent(activity, AddGameActivity::class.java)
            activity.startActivity(intent)
        }
    }
}