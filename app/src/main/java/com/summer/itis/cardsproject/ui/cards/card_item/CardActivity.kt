package com.summer.itis.cardsproject.ui.cards.card_item

import QuestionFragment.Companion.CARD_JSON
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.FOCUS_DOWN
import android.widget.EditText
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.AbstractCard
import com.summer.itis.cardsproject.model.Comment
import com.summer.itis.cardsproject.model.User
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.userRepository
import com.summer.itis.cardsproject.ui.base.NavigationBaseActivity
import com.summer.itis.cardsproject.ui.cards.card_states.CardStatesActivity
import com.summer.itis.cardsproject.ui.cards.cards_info.WebViewActivity
import com.summer.itis.cardsproject.ui.comment.CommentAdapter
import com.summer.itis.cardsproject.ui.comment.OnCommentClickListener
import com.summer.itis.cardsproject.ui.member.member_item.PersonalActivity
import com.summer.itis.cardsproject.ui.tests.one_test_list.OneTestListActivity
import com.summer.itis.cardsproject.utils.AppHelper
import com.summer.itis.cardsproject.utils.Const.ABSTRACT_CARD_ID
import com.summer.itis.cardsproject.utils.Const.ONLINE_STATUS
import com.summer.itis.cardsproject.utils.Const.TAG_LOG
import com.summer.itis.cardsproject.utils.Const.TEST_LIST_TYPE
import com.summer.itis.cardsproject.utils.Const.USER_ID
import com.summer.itis.cardsproject.utils.Const.gsonConverter
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_card.*
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import kotlinx.android.synthetic.main.layout_add_comment.*
import kotlinx.android.synthetic.main.layout_card.*
import kotlinx.android.synthetic.main.layout_expandable_text_view.*
import java.util.*

class CardActivity : NavigationBaseActivity(), CardView, View.OnClickListener, OnCommentClickListener {

    internal var PLACE_PICKER_REQUEST = 1

    @InjectPresenter
    lateinit var presenter: CardPresenter

    lateinit var card: AbstractCard
    lateinit var testType: String

    private lateinit var adapter: CommentAdapter

    private var comments: MutableList<Comment> = ArrayList()

    private lateinit var commentEditText: EditText


    companion object {

        fun start(activity: Context, card: AbstractCard, tag: String){
            val intent = Intent(activity, CardActivity::class.java)
            intent.putExtra(CARD_JSON, gsonConverter.toJson(card))
            intent.putExtra(TEST_LIST_TYPE, tag)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatus(ONLINE_STATUS)
            waitEnemy()
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_card)

            card = gsonConverter.fromJson(intent.getStringExtra(CARD_JSON), AbstractCard::class.java)
            testType = intent.getStringExtra(TEST_LIST_TYPE)

            initViews()
            initRecycler()

            card.id?.let { presenter.loadComments(it) }

    }

    private fun initViews() {
        toolbar.title = card.name
        setSupportActionBar(toolbar)
        setBackArrow(toolbar)

        tv_name.text = card.name
        expand_text_view.text = card.description
        Glide.with(iv_portrait.context)
                .load(card.photoUrl)
                .into(iv_portrait)
        li_wiki.setOnClickListener(this)
        li_states.setOnClickListener(this)
        li_tests.setOnClickListener(this)

        commentEditText = findViewById<View>(R.id.commentEditText) as EditText


        commentEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                Log.d(TAG_LOG, "char length = " + charSequence.length)
                sendButton.isEnabled = charSequence.toString().trim { it <= ' ' }.length > 0
                Log.d(TAG_LOG, "enabled = " + sendButton.isEnabled)
            }

            override fun afterTextChanged(editable: Editable) {
                val charSequence = editable.toString()
                Log.d(TAG_LOG, "after char length = " + charSequence.length)
                sendButton.isEnabled = charSequence.trim { it <= ' ' }.length > 0
                Log.d(TAG_LOG, "enabled = " + sendButton.isEnabled)
            }
        })

        sendButton.setOnClickListener {
            if ((this as NavigationBaseActivity).hasInternetConnection()) {
                sendComment()
            } else {
                (this as NavigationBaseActivity).showSnackBar(R.string.internet_connection_failed)
            }
        }
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.li_states -> CardStatesActivity.start(this,card)
            R.id.li_tests -> OneTestListActivity.start(this, getRedirectIntent())
            R.id.li_wiki -> getWikiUrl()
        }
    }

    private fun getRedirectIntent(): Intent {
        val intent = Intent(this,OneTestListActivity::class.java)
        intent.putExtra(TEST_LIST_TYPE, testType)
        intent.putExtra(ABSTRACT_CARD_ID, card.id)
        intent.putExtra(USER_ID, AppHelper.currentUser?.id)
        return intent
    }

    private fun getWikiUrl() {
        if(card.wikiUrl != null){
            WebViewActivity.start(this, card)
        }else{
            Toast.makeText(this,"Wiki не существует",Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViews(view: View) {


    }

    override fun onReplyClick(position: Int) {
        commentEditText.isEnabled = true
        val comment = comments.get(position)
        val commentString = comment.authorName + ", "
        commentEditText.setText(commentString)
        commentEditText.isPressed = true
        commentEditText.setSelection(commentString.length)
    }

    override fun onAuthorClick(authorId: String) {
        val reference = userRepository.readUser(authorId)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                user?.let { PersonalActivity.start(this@CardActivity, it) }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    override fun setComments(comments: List<Comment>) {
        this.comments = comments.toMutableList()
        adapter?.changeDataSet(comments)
    }


    private fun sendComment() {
        scrollView.fullScroll(FOCUS_DOWN)
        Log.d(TAG_LOG,"focus down")
        val commentText = commentEditText.getText().toString()
        Log.d(TAG_LOG, "send comment = $commentText")
        if (commentText.length > 0) {
            val comment = Comment()
            val user = AppHelper.currentUser
            user?.let {
                comment.text = commentText
                comment.authorId = user.id
                comment.authorName = user.username
                comment.authorPhotoUrl = user.photoUrl
                comment.createdDate = (Date().time)
                card.id?.let { it1 -> presenter.createComment(it1, comment) }
                addComment(comment)
            }

            commentEditText.setText(null)
            currentFocus?.let { AppHelper.hideKeyboardFrom(this@CardActivity as Context, it)
                Log.d(TAG_LOG,"hide keyboard")
            }
            commentEditText.clearFocus()
        }
    }

    override fun addComment(comment: Comment) {
        comments.add(comment)
        adapter?.changeDataSet(comments)
    }

    override fun showComments(comments: List<Comment>) {
        this.comments = comments.toMutableList()
        adapter?.changeDataSet(comments)
    }

    override fun showLoading(disposable: Disposable) {
        pg_comics_list.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        pg_comics_list.visibility = View.GONE
    }

    private fun initRecycler() {
        adapter = CommentAdapter(ArrayList(), this)
        adapter?.let {
            val manager = LinearLayoutManager(this)
            rv_comics_list.let {
                rv_comics_list?.setLayoutManager(manager)
                adapter?.attachToRecyclerView(it)
            }
            adapter?.setOnItemClickListener(this)
            rv_comics_list?.setAdapter(adapter)
        }
    }

    override fun onItemClick(item: Comment) {
    }


}
