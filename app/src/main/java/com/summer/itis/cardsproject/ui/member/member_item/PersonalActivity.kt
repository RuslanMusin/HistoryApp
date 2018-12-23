package com.summer.itis.cardsproject.ui.member.member_item

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog

import com.bumptech.glide.Glide

import com.google.gson.Gson
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.Epoch
import com.summer.itis.cardsproject.model.User
import com.summer.itis.cardsproject.model.game.Lobby
import com.summer.itis.cardsproject.model.game.LobbyPlayerData
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.cardRepository
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.epochRepository
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.userRepository
import com.summer.itis.cardsproject.repository.json.UserRepository
import com.summer.itis.cardsproject.ui.base.NavigationBaseActivity
import com.summer.itis.cardsproject.ui.cards.one_card_list.OneCardListActivity
import com.summer.itis.cardsproject.ui.epoch.EpochListActivity
import com.summer.itis.cardsproject.ui.tests.add_test.fragments.main.AddTestFragment
import com.summer.itis.cardsproject.ui.tests.add_test.fragments.main.AddTestFragment.Companion.ADD_EPOCH
import com.summer.itis.cardsproject.ui.tests.one_test_list.OneTestListActivity
import com.summer.itis.cardsproject.utils.ApplicationHelper
import com.summer.itis.cardsproject.utils.Const

import com.summer.itis.cardsproject.utils.Const.ADD_FRIEND
import com.summer.itis.cardsproject.utils.Const.ADD_REQUEST
import com.summer.itis.cardsproject.utils.Const.DEFAULT_ABSTRACT_TESTS
import com.summer.itis.cardsproject.utils.Const.EPOCH_KEY
import com.summer.itis.cardsproject.utils.Const.ONLINE_STATUS
import com.summer.itis.cardsproject.utils.Const.OWNER_TYPE
import com.summer.itis.cardsproject.utils.Const.REMOVE_FRIEND
import com.summer.itis.cardsproject.utils.Const.REMOVE_REQUEST
import com.summer.itis.cardsproject.utils.Const.STUB_PATH
import com.summer.itis.cardsproject.utils.Const.TAG_LOG
import com.summer.itis.cardsproject.utils.Const.TEST_LIST_TYPE
import com.summer.itis.cardsproject.utils.Const.USER_ID
import com.summer.itis.cardsproject.utils.Const.USER_KEY
import com.summer.itis.cardsproject.utils.Const.USER_TESTS
import com.summer.itis.cardsproject.utils.Const.USER_TYPE
import com.summer.itis.cardsproject.utils.Const.gsonConverter
import kotlinx.android.synthetic.main.dialog_fast_game.*
import kotlinx.android.synthetic.main.layout_expandable_text_view.*
import kotlinx.android.synthetic.main.layout_personal.*


class PersonalActivity : NavigationBaseActivity(), View.OnClickListener {

    private lateinit var toolbar: Toolbar
    private var tvName: TextView? = null
    private var btnAddFriend: AppCompatButton? = null

    lateinit var user: User
    //SET-GET


    var type: String? = null

    lateinit var presenter: PersonalPresenter

    lateinit var gameDialog: MaterialDialog
    lateinit var types: List<String>

    val lobby: Lobby = Lobby()

    var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatus(ONLINE_STATUS)
        waitEnemy()
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.container)
        layoutInflater.inflate(R.layout.activity_profile, contentFrameLayout)

        presenter = PersonalPresenter(this)

        val userJson = intent.getStringExtra(USER_KEY)
        presenter!!.setUserRelationAndView(userJson)


    }

    internal fun initViews() {
        Log.d(TAG_LOG, "type = " + type!!)
        findViews()
        if(type.equals(OWNER_TYPE)) {
            supportActionBar(toolbar)
        } else {
            setSupportActionBar(toolbar)
            setBackArrow(toolbar)
        }
        toolbar.title = user?.username

        btnAddFriend!!.setOnClickListener(this)
        li_tests!!.setOnClickListener(this)
        li_cards.setOnClickListener(this)
        btn_play_game.setOnClickListener(this)
//        tv_add_epoches.setOnClickListener(this)

    }

    private fun findViews() {
        toolbar = findViewById(R.id.toolbar)

        btnAddFriend = findViewById(R.id.btn_add_friend)
        tvName = findViewById(R.id.nameEditText)

        if (type == OWNER_TYPE) {
            user = ApplicationHelper.currentUser
            setUserData()
        } else {
            setUserData()
        }
    }

    private fun setUserData() {
        tvName!!.text = user.username

        expand_text_view.text = user.desc

        if (!user.isStandartPhoto) {
            val imageReference = user.photoUrl?.let { ApplicationHelper.storageReference.child(it) }

            Log.d(TAG_LOG, "name " + (imageReference?.path ?: ""))

            Glide.with(this)
                    .load(imageReference)
                    .into(iv_portrait!!)
        } else {
            Glide.with(this)
                    .load(user.photoUrl)
                    .into(iv_portrait!!)
        }

        when (type) {
            ADD_FRIEND -> btnAddFriend!!.setText(R.string.add_friend)

            ADD_REQUEST -> btnAddFriend!!.setText(R.string.add_friend)

            REMOVE_FRIEND -> btnAddFriend!!.setText(R.string.remove_friend)

            REMOVE_REQUEST -> btnAddFriend!!.setText(R.string.remove_request)

            OWNER_TYPE -> {
                btnAddFriend!!.visibility = View.GONE
                btn_play_game.visibility = View.GONE
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
        /*case R.id.btn_change_data:
                changeData();
                break;*/

            R.id.btn_add_friend -> actWithUser()

            R.id.li_tests -> showTests()

            R.id.li_cards -> showCards()

            R.id.btn_play_game -> playGame()

//            R.id.tv_add_epoches -> createEpoches()
        }
    }

    fun createEpoches() {
        val list = resources.getStringArray(R.array.epoches).toList()
        for(item in list) {

            epochRepository.createEpoch(Epoch(item.toString())).subscribe()
        }
    }

    private fun playGame() {
        changePlayButton(false)
        userRepository.checkUserStatus(user.id).subscribe { isOnline ->
            if (isOnline) {
                gameDialog = MaterialDialog.Builder(this)
                        .customView(R.layout.dialog_fast_game, false)
                        .onNeutral { dialog, which ->
                            dialog.cancel()
                            changePlayButton(true)
                        }
                        .build()

                gameDialog.btn_create_game.setOnClickListener{ createGame() }
                gameDialog.li_choose_epoch.setOnClickListener {
                    val intent = Intent(this, EpochListActivity::class.java)
                    startActivityForResult(intent, AddTestFragment.ADD_EPOCH)
                }

                types = listOf(getString(R.string.user_type), getString(R.string.official_type))
                gameDialog.spinner.setItems(types)
                gameDialog.seekBarCards.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        val strProgress: String = seekBar?.progress.toString()
                        gameDialog.tvCards.text = strProgress
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }

                })


                gameDialog.show()

            } else {
                showSnackBar(R.string.enemy_not_online)
                changePlayButton(true)
            }
        }

    }

    fun createGame() {
        lobby.cardNumber = gameDialog.seekBarCards.progress
        if(lobby.cardNumber >= 5) {
            if (types[gameDialog.spinner.selectedIndex].equals(getString(R.string.official_type))) {
                lobby.type = Const.OFFICIAL_TYPE
            }
            cardRepository.findCardsByType(user.id,lobby.type).subscribe{ cards ->
                val cardNumber = cards.size
                if(cardNumber >= lobby.cardNumber) {
                    cardRepository.findCardsByType(UserRepository.currentId, lobby.type).subscribe { myCards ->
                        val mySize = myCards.size
                        if (mySize >= lobby.cardNumber) {
                            gameDialog.hide()
                            showProgressDialog()
                            lobby.isFastGame = true
                            val playerData = LobbyPlayerData()
                            playerData.playerId = UserRepository.currentId
                            playerData.online = true
                            lobby.creator = playerData
                            user?.id?.let { presenter.playGame(it, lobby) }
                        } else {
                            showSnackBar(R.string.you_dont_have_card_min)
                        }
                    }
                } else {
                    showSnackBar(R.string.enemy_doesnt_have_card_min)
                }
            }

        } else {
            showSnackBar(R.string.set_card_min)
        }
    }

    fun changePlayButton(isClickable: Boolean) {
       btn_play_game.isClickable = isClickable
    }

    private fun showTests() {
        val intent: Intent = Intent(this,OneTestListActivity::class.java)
        intent.putExtra(TEST_LIST_TYPE, USER_TESTS)
        intent.putExtra(USER_ID,user?.id)
        OneTestListActivity.start(this,intent)
    }

    private fun showCards() {
        val intent: Intent = Intent(this,OneCardListActivity::class.java)
        intent.putExtra(USER_ID,user?.id)
        OneCardListActivity.start(this,intent)
    }

    private fun actWithUser() {
        when (type) {
            ADD_FRIEND -> {
                user!!.id?.let { UserRepository().addFriend(UserRepository.currentId, it) }
                type = REMOVE_FRIEND
                btnAddFriend!!.setText(R.string.remove_friend)
            }

            ADD_REQUEST -> {
                user!!.id?.let { UserRepository().addFriendRequest(UserRepository.currentId, it) }
                type = REMOVE_REQUEST
                btnAddFriend!!.setText(R.string.remove_request)
            }

            REMOVE_FRIEND -> {
                user!!.id?.let { UserRepository().removeFriend(UserRepository.currentId, it) }
                type = ADD_REQUEST
                btnAddFriend!!.setText(R.string.add_friend)
            }

            REMOVE_REQUEST -> {
                user!!.id?.let { UserRepository().removeFriendRequest(UserRepository.currentId, it) }
                type = ADD_REQUEST
                btnAddFriend!!.setText(R.string.add_friend)
            }
        }
    }

    private fun changeData() {
        //        startActivity(ChangeUserDataActivity.makeIntent(TestActivity.this));
    }

    fun showProgressDialog() {
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

    fun hideProgressDialog() {
        showSnackBar("Противник не принял приглашение")
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            if (reqCode == ADD_EPOCH) {
                val epoch = gsonConverter.fromJson(data!!.getStringExtra(EPOCH_KEY), Epoch::class.java)
                gameDialog.tv_epoch.text = epoch.name
                lobby.epoch = epoch
                lobby.epochId = epoch.id
            }
        }

    }


    companion object {

        fun start(activity: Activity, comics: User) {
            val intent = Intent(activity, PersonalActivity::class.java)
            val gson = Gson()
            val userJson = gson.toJson(comics)
            intent.putExtra(USER_KEY, userJson)
            activity.startActivity(intent)
        }

        fun start(activity: Activity) {
            val intent = Intent(activity, PersonalActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            activity.startActivity(intent)
        }

        fun start(activity: Context?, comics: User?) {
            val intent = Intent(activity, PersonalActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            activity?.startActivity(intent)
        }
    }
}
