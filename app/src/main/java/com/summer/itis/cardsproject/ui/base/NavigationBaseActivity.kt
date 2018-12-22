package com.summer.itis.cardsproject.ui.base

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.MvpAppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.flags.impl.SharedPreferencesFactory.getSharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.game.GameData
import com.summer.itis.cardsproject.model.game.Lobby
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.gamesRepository
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.userRepository
import com.summer.itis.cardsproject.repository.json.GamesRepository.Companion.FIELD_CREATOR
import com.summer.itis.cardsproject.repository.json.GamesRepository.Companion.FIELD_INVITED
import com.summer.itis.cardsproject.repository.json.UserRepository
import com.summer.itis.cardsproject.ui.cards.cards_list.activity.CardsListActivity
import com.summer.itis.cardsproject.ui.game.game_list.game.GameListActivity
import com.summer.itis.cardsproject.ui.game.play.PlayGameActivity
import com.summer.itis.cardsproject.ui.member.member_item.PersonalActivity
import com.summer.itis.cardsproject.ui.member.member_list.reader.ReaderListActivity
import com.summer.itis.cardsproject.ui.start.login.LoginActivity
import com.summer.itis.cardsproject.ui.tests.test_list.test.TestListActivity
import com.summer.itis.cardsproject.utils.ApplicationHelper
import com.summer.itis.cardsproject.utils.ApplicationHelper.Companion.offlineFunction
import com.summer.itis.cardsproject.utils.ApplicationHelper.Companion.onlineFunction
import com.summer.itis.cardsproject.utils.ApplicationHelper.Companion.userStatus
import com.summer.itis.cardsproject.utils.Const.IN_GAME_STATUS
import com.summer.itis.cardsproject.utils.Const.OFFLINE_STATUS
import com.summer.itis.cardsproject.utils.Const.ONLINE_GAME
import com.summer.itis.cardsproject.utils.Const.STOP_STATUS
import com.summer.itis.cardsproject.utils.Const.TAG_LOG
import com.summer.itis.cardsproject.utils.Const.USER_DATA_PREFERENCES
import com.summer.itis.cardsproject.utils.Const.USER_PASSWORD
import com.summer.itis.cardsproject.utils.Const.USER_USERNAME
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.view_nav_header.view.*
import java.util.*

//АКТИВИТИ РОДИТЕЛЬ ДЛЯ ОСНОВНОЙ НАВИГАЦИИ(БОКОВОЙ). ЮЗАТЬ МЕТОДЫ supportActionBar И setBackArrow(ЕСЛИ НУЖНА СТРЕЛКА НАЗАД)
open class NavigationBaseActivity : MvpAppCompatActivity() {

    protected lateinit var mDrawer: DrawerLayout
    protected lateinit var mNavigationView: NavigationView
    var progressDialog: ProgressDialog? = null
    protected lateinit var headerImage: ImageView

    var isStopped: Boolean = false
    var currentStatus: String = OFFLINE_STATUS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        mDrawer = findViewById(R.id.drawer_layout)
        mNavigationView = findViewById(R.id.nav_view)
        setOfflineChecking()
    }

    private fun setOfflineChecking() {
        userRepository.checkUserConnection(offlineMode())

    }

    fun offlineMode(): () -> (Unit) {
       return {
           Log.d(TAG_LOG,"offline mode")
           if(li_offline != null && container != null) {
               li_offline.visibility = View.VISIBLE
               container.visibility = View.GONE
           }
       }
    }

    fun onlineMode(): () -> (Unit) {
        return {
            Log.d(TAG_LOG,"online mode")
            if(li_offline != null && container != null) {
                container.visibility = View.VISIBLE
                li_offline.visibility = View.GONE
            }
        }
    }

    override fun onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun setStatus(status: String) {
        Log.d(TAG_LOG,"current status = $status")
        currentStatus = status
        userStatus = status
        onlineFunction = onlineMode()
        offlineFunction = offlineMode()
        userRepository.changeJustUserStatus(status).subscribe()
    }


    override fun onResume() {
        Log.d(TAG_LOG,"onResume")
        setStatus(currentStatus)
        super.onResume()
    }


    final override fun onStop() {
        isStopped = true
        super.onStop()
    }

    override fun onPause() {
        Log.d(TAG_LOG,"on stop status = $OFFLINE_STATUS")
        userRepository.changeJustUserStatus(STOP_STATUS).subscribe()
        super.onPause()
    }

    fun waitEnemy() {
        Log.d(TAG_LOG,"wait enemy")
        gamesRepository.waitEnemy().subscribe { relation ->
            if (relation.relation.equals(IN_GAME_STATUS)) {
                gamesRepository.findLobby(relation.id).subscribe { lobby ->
                    if (!isStopped) {
                        ApplicationHelper.currentUser.let {
                            it.gameLobby = lobby
                            val gameData: GameData = GameData()
                            gameData.gameMode = ONLINE_GAME
                            val invitedId = lobby.invited?.playerId
                            val creatorId = lobby.creator?.playerId
                            if (invitedId != null && creatorId.equals(UserRepository.currentId)) {
                                invitedId.let {
                                    gameData.enemyId = it
                                    gameData.role = FIELD_CREATOR
                                }
                            } else {
                                creatorId?.let {
                                    gameData.enemyId = it
                                    gameData.role = FIELD_INVITED
                                }
                            }
                            it.gameLobby?.gameData = gameData
                            val dialog: MaterialDialog = MaterialDialog.Builder(this)
                                    .title(R.string.question_dialog_title)
                                    .content(R.string.question_dialog_content)
                                    .positiveText(R.string.agree)
                                    .negativeText(R.string.disagree)
                                    .onPositive(object : MaterialDialog.SingleButtonCallback {
                                        override fun onClick(dialog: MaterialDialog, which: DialogAction) {

                                            userRepository.checkUserStatus(gameData.enemyId).subscribe { isOnline ->
                                                dialog.hide()
                                                if (isOnline) {
                                                    userRepository.changeUserStatus(it).subscribe()
                                                    gamesRepository.acceptMyGame(lobby).subscribe { e ->
                                                        Log.d(TAG_LOG,"play game after wait")
                                                        PlayGameActivity.start(this@NavigationBaseActivity)
                                                    }
                                                } else {
                                                    this@NavigationBaseActivity.showSnackBar(R.string.enemy_not_online)
                                                    waitEnemy()
                                                }
                                            }
                                        }

                                    })
                                    .onNegative { dialog: MaterialDialog, which: DialogAction ->
                                        dialog.hide()
                                        refuseAndWait(lobby) }
                                    .build()

                            dialog.setCancelable(false)
                            dialog.show()
                        }
                    }
                }
            }
        }
    }

    fun refuseAndWait(lobby: Lobby) {
        gamesRepository.refuseGame(lobby).subscribe{ e -> waitEnemy()}
    }

    protected fun supportActionBar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        initNavigationDrawer(toolbar)
    }

    protected open fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    protected fun setBackArrow(toolbar: Toolbar) {
        val actionBar = supportActionBar
        if (actionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            toolbar.setNavigationOnClickListener { v -> onBackPressed() }
        }
    }

    private fun initNavigationDrawer(toolbar: Toolbar) {
        mNavigationView.setNavigationItemSelectedListener { menuItem ->
            val id = menuItem.itemId
            when (id) {
                R.id.menu_tests -> TestListActivity.start(this)

                R.id.menu_cards -> CardsListActivity.start(this)

                R.id.menu_game -> GameListActivity.start(this)

                R.id.menu_friends -> ReaderListActivity.start(this)

                R.id.menu_logout -> {
                    val user = ApplicationHelper.currentUser
                    user?.let {
                        it.status = OFFLINE_STATUS
                        userRepository.changeUserStatus(user).subscribe()

                    }
                    FirebaseAuth.getInstance().signOut()
                    deleteUserPrefs()
                    LoginActivity.start(this)
                }
            }
            true
        }

        val header = mNavigationView.getHeaderView(0)
        headerImage = header.findViewById(R.id.iv_crossing)
//        ApplicationHelper.loadUserPhoto(headerImage)
        ApplicationHelper.currentUser.let{
            header.tv_menu.text = it.username

            if (!it.isStandartPhoto) {
                val imageReference = it.photoUrl?.let { ApplicationHelper.storageReference.child(it) }

                Log.d(TAG_LOG, "name " + (imageReference?.path ?: ""))

                Glide.with(this)
                        .load(imageReference)
                        .into(headerImage)
            } else {
                Glide.with(this)
                        .load(it.photoUrl)
                        .into(headerImage)
            }

        }

        headerImage.setOnClickListener { PersonalActivity.start(this@NavigationBaseActivity) }

        setActionBar(toolbar)
    }

    private fun deleteUserPrefs() {
        val sharedPreferences: SharedPreferences = getSharedPreferences(USER_DATA_PREFERENCES,Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.remove(USER_USERNAME)
        editor.remove(USER_PASSWORD)
        editor.apply()
    }

    private fun setActionBar(toolbar: Toolbar) {
        Log.d(TAG_LOG, "set action bar")
        val actionBarDrawerToggle = ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.drawer_open, R.string.drawer_close)
        mDrawer.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    @JvmOverloads
    fun showProgress(message: Int = R.string.loading) {
        hideProgress()
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage(getString(message))
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    fun hideProgress() {
        if (progressDialog != null) {
            progressDialog!!.dismiss()
            progressDialog = null
        }
    }

    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showSnackBar(message: String) {
        val snackbar: Snackbar = Snackbar.make(findViewById(android.R.id.content),
                message, Snackbar.LENGTH_LONG)
        snackbar.getView().setBackgroundColor(Color.BLACK)
        val textView = snackbar.view.findViewById(android.support.design.R.id.snackbar_text) as TextView;
        textView.setTextColor(Color.WHITE);
        snackbar.show()
    }

    fun showSnackBar(messageId: Int) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content),
                messageId, Snackbar.LENGTH_LONG)
        snackbar.getView().setBackgroundColor(Color.BLACK)
        val textView = snackbar.view.findViewById(android.support.design.R.id.snackbar_text) as TextView;
        textView.setTextColor(Color.WHITE);
        snackbar.show()
    }


    fun showWarningDialog(messageId: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(messageId)
        builder.setPositiveButton(R.string.button_ok, null)
        builder.show()
    }

    fun showWarningDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.button_ok, null)
        builder.show()
    }

    fun hasInternetConnection(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    fun checkInternetConnection(): Boolean {
        val hasInternetConnection = hasInternetConnection()
        if (!hasInternetConnection) {
            showWarningDialog(R.string.internet_connection_failed)
        }

        return hasInternetConnection
    }

}
