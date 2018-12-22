package com.summer.itis.summerproject.ui.tests.test_item.fragments.main

import GameQuestionFragment.Companion.QUESTION_NUMBER
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import com.summer.itis.summerproject.R
import com.summer.itis.summerproject.model.Comment
import com.summer.itis.summerproject.model.Question
import com.summer.itis.summerproject.model.Test
import com.summer.itis.summerproject.model.User
import com.summer.itis.summerproject.repository.RepositoryProvider.Companion.userRepository
import com.summer.itis.summerproject.ui.base.BaseBackActivity
import com.summer.itis.summerproject.ui.base.NavigationBaseActivity
import com.summer.itis.summerproject.ui.base.OnBackPressedListener
import com.summer.itis.summerproject.ui.comment.CommentAdapter
import com.summer.itis.summerproject.ui.comment.OnCommentClickListener
import com.summer.itis.summerproject.ui.member.member_item.PersonalActivity
import com.summer.itis.summerproject.ui.tests.ChangeToolbarListener
import com.summer.itis.summerproject.ui.tests.test_item.TestActivity.Companion.QUESTION_FRAGMENT
import com.summer.itis.summerproject.ui.tests.test_item.TestActivity.Companion.TEST_FRAGMENT
import com.summer.itis.summerproject.ui.tests.test_item.TestActivity.Companion.TEST_JSON
import com.summer.itis.summerproject.ui.tests.test_item.TestView
import com.summer.itis.summerproject.ui.tests.test_item.fragments.finish.FinishFragment
import com.summer.itis.summerproject.ui.tests.test_list.test.TestListActivity
import com.summer.itis.summerproject.ui.widget.ExpandableTextView
import com.summer.itis.summerproject.utils.ApplicationHelper
import com.summer.itis.summerproject.utils.Const.AFTER_TEST
import com.summer.itis.summerproject.utils.Const.LOSE_GAME

import com.summer.itis.summerproject.utils.Const.TAG_LOG
import com.summer.itis.summerproject.utils.Const.WIN_GAME
import com.summer.itis.summerproject.utils.Const.gsonConverter
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import kotlinx.android.synthetic.main.layout_add_comment.*
import kotlinx.android.synthetic.main.layout_expandable_text_view.*
import kotlinx.android.synthetic.main.layout_test.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TestFragment : MvpAppCompatFragment(), View.OnClickListener, OnCommentClickListener, TestFragmentView, OnBackPressedListener {


    private lateinit var commentEditText: EditText

    var isFocusDown: Boolean = false

    private lateinit var adapter: CommentAdapter

    private var comments: MutableList<Comment> = ArrayList()

    lateinit var test: Test

    @InjectPresenter
    lateinit var presenter: TestFragmentPresenter

    companion object {

        fun newInstance(args: Bundle): Fragment {
            val fragment = TestFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onBackPressed() {
       /* val args: Bundle = Bundle()
        args.putString(TEST_JSON, gsonConverter.toJson(test))
        val fragment = FinishFragment.newInstance(args)
        (activity as BaseBackActivity).changeFragment(fragment)*/

        TestListActivity.start(activity as Activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_test, container, false)


        val testStr: String? = arguments?.getString(TEST_JSON)
        test = gsonConverter.fromJson(testStr,Test::class.java)


        (activity as BaseBackActivity).currentTag = TEST_FRAGMENT
        test.title?.let { (activity as ChangeToolbarListener).changeToolbar(TEST_FRAGMENT, it) }
        presenter.readCardForTest(test)



        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ApplicationHelper.hideKeyboardFrom(activity as Context,view)

        initViews(view)
        initRecycler()
        setListeners()

        test.id?.let { presenter.loadComments(it) }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun setData() {
        if (test.testDone == false) {
            tv_done.text = getText(R.string.test_wasnt_done)
        } else {
            tv_done.text = getText(R.string.test_was_done)
        }
        val relation: String? = test.testRelation?.relation
        Log.d(TAG_LOG,"has card = $relation and has test = ${test.testDone}")
        if(relation.equals(AFTER_TEST) || relation.equals(WIN_GAME)) {
            tv_card_done.text = getText(R.string.test_was_done)
        } else {
            tv_card_done.text = getText(R.string.test_wasnt_done)
        }
        tv_author.text = test.authorName
        expand_text_view.text = test.desc
        nameEditText.text = test.title
        test.card?.abstractCard?.photoUrl?.let {
            Glide.with(iv_portrait.context)
                    .load(it)
                    .into(iv_portrait)
        }
    }

    private fun initViews(view: View) {

        commentEditText = view.findViewById<View>(R.id.commentEditText) as EditText


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
            if ((activity as NavigationBaseActivity).hasInternetConnection()) {
                sendComment()
            } else {
                (activity as NavigationBaseActivity).showSnackBar(R.string.internet_connection_failed)
            }
        }
    }

    private fun setListeners() {
        btn_do_test.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.btn_do_test -> {

                val args: Bundle = Bundle()
                args.putString(TEST_JSON, gsonConverter.toJson(test))
                args.putInt(QUESTION_NUMBER,0)
                val fragment = QuestionFragment.newInstance(args)
                (activity as BaseBackActivity).changeFragment(fragment, QUESTION_FRAGMENT + 0)

            }
        }
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
                user?.let { PersonalActivity.start(activity as Activity, it) }
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
            val user = ApplicationHelper.currentUser
            user?.let {
                comment.text = commentText
                comment.authorId = user.id
                comment.authorName = user.username
                comment.authorPhotoUrl = user.photoUrl
                comment.createdDate = (Date().time)
                test.id?.let { it1 -> presenter.createComment(it1, comment) }
                addComment(comment)
            }

            commentEditText.setText(null)
            view?.getRootView()?.let { ApplicationHelper.hideKeyboardFrom(this.activity as Context, it)
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
        pg_comics_list.visibility = VISIBLE
    }

    override fun hideLoading() {
        pg_comics_list.visibility = GONE
    }


    override fun onItemClick(item: Comment) {
    }

    private fun initRecycler() {
        adapter = CommentAdapter(ArrayList(), this)
        adapter?.let {
            val manager = LinearLayoutManager(this.activity)
            rv_comics_list.let {
                rv_comics_list?.setLayoutManager(manager)
                adapter?.attachToRecyclerView(it)
            }
            adapter?.setOnItemClickListener(this)
            rv_comics_list?.setAdapter(adapter)
        }


    }


}
