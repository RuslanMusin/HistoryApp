package com.summer.itis.summerproject.ui.tests.add_test

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

import com.arellomobile.mvp.presenter.InjectPresenter
import com.summer.itis.summerproject.R
import com.summer.itis.summerproject.model.Question
import com.summer.itis.summerproject.model.Test
import com.summer.itis.summerproject.repository.RepositoryProvider
import com.summer.itis.summerproject.repository.RepositoryProvider.Companion.testRepository
import com.summer.itis.summerproject.ui.base.*
import com.summer.itis.summerproject.ui.tests.ChangeToolbarListener
import com.summer.itis.summerproject.ui.tests.add_test.fragments.main.AddTestFragment
import com.summer.itis.summerproject.ui.tests.add_test.fragments.question.AddQuestionFragment
import com.summer.itis.summerproject.ui.tests.test_item.TestActivity
import com.summer.itis.summerproject.ui.tests.test_item.TestActivity.Companion.ANSWERS_FRAGMENT
import com.summer.itis.summerproject.ui.tests.test_item.TestActivity.Companion.FINISH_FRAGMENT
import com.summer.itis.summerproject.ui.tests.test_item.TestActivity.Companion.QUESTION_FRAGMENT
import com.summer.itis.summerproject.ui.tests.test_item.TestActivity.Companion.TEST_FRAGMENT
import com.summer.itis.summerproject.ui.tests.test_item.TestActivity.Companion.TEST_JSON
import com.summer.itis.summerproject.ui.tests.test_item.TestActivity.Companion.WINNED_FRAGMENT
import com.summer.itis.summerproject.ui.tests.test_item.fragments.main.TestFragment
import com.summer.itis.summerproject.ui.tests.test_list.test.TestListActivity
import com.summer.itis.summerproject.utils.ApplicationHelper
import com.summer.itis.summerproject.utils.Const
import com.summer.itis.summerproject.utils.Const.EDIT_STATUS
import com.summer.itis.summerproject.utils.Const.gsonConverter
import kotlinx.android.synthetic.main.activity_with_frame_and_toolbar.*
import kotlinx.android.synthetic.main.back_forward.*

class AddTestActivity : BaseBackActivity(), AddTestView, ChangeToolbarListener {

    internal var PLACE_PICKER_REQUEST = 1

    private var toolbar: Toolbar? = null

    @InjectPresenter
    lateinit var presenter: AddTestPresenter

    private var test: Test? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        setStatus(EDIT_STATUS)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_frame_and_toolbar)

        val fragment = AddTestFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment, ADD_TEST_FRAGMENT)
                .commit()

        initViews()
    }

    override fun changeToolbar(tag: String, title: String) {
        setToolbarTitle(title)
        when {
            ADD_TEST_FRAGMENT.equals(tag) -> {
                btn_ok.visibility = View.GONE
                btn_cancel.visibility = View.GONE
                btn_forward.visibility = View.GONE
            }

            ADD_QUESTION_FRAGMENT.equals(tag) -> {
                btn_ok.visibility = View.VISIBLE
                btn_back.visibility = View.VISIBLE
                btn_cancel.visibility = View.VISIBLE
                btn_forward.visibility = View.VISIBLE
            }
        }
    }

    override fun setToolbarTitle(title: String) {
        toolbar_title.text = title
    }

    override fun showOk(boolean: Boolean) {
        if(boolean == true) {
            btn_ok.visibility = View.VISIBLE
        } else {
            btn_ok.visibility = View.GONE
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {

            R.id.btn_back -> {
                onBackPressed()
            }

            R.id.btn_forward -> {
                (getCurrentFragment() as OnForwardListener).onForward()
            }

            R.id.btn_ok -> {
                (getCurrentFragment() as OnOkListener).onOk()
            }

            R.id.btn_cancel -> {
//                TestListActivity.start(this)
                (getCurrentFragment() as OnCancelListener).onCancel()
            }
        }
    }


    private fun initViews() {
        app_bar.removeView(test_toolbar)
        toolbar = layoutInflater.inflate(R.layout.toolbar_add_test,app_bar,false) as Toolbar
        app_bar.addView(toolbar)
        setSupportActionBar(toolbar)
        btn_back.setOnClickListener(this)
        btn_forward.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
        btn_ok.setOnClickListener(this)
//        setBackArrow(toolbar)

    }


    override fun setQuestion(question: Question) {
        test?.let{
            val questionId: String = (it.questions.size).toString()
            question.id = questionId
            it.questions.add(question)
        }
    }

    override fun createTest() {
        testRepository.
                createTest(test!!, ApplicationHelper.currentUser!!)
                .subscribe{e -> TestActivity.start(this, test!!)}

    }

    override fun setTest(test: Test) {
        this.test = test
    }

    companion object {

        const val ADD_QUESTION_FRAGMENT: String = "add_question_fragment"
        const val ADD_CARD_FRAGMENT: String = "add_card_fragment"
        const val ADD_CARD_LIST_FRAGMENT: String = "add_card_list_fragment"
        const val ADD_TEST_FRAGMENT: String = "add_test_fragment"

        fun start(activity: Activity) {
            val intent = Intent(activity, AddTestActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
