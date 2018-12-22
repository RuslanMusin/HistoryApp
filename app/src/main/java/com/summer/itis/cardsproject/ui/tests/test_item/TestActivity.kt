package com.summer.itis.cardsproject.ui.tests.test_item

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.ForwardingListener
import android.support.v7.widget.Toolbar
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter

import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.Test
import com.summer.itis.cardsproject.repository.RepositoryProvider
import com.summer.itis.cardsproject.ui.base.BaseBackActivity
import com.summer.itis.cardsproject.ui.base.OnCancelListener
import com.summer.itis.cardsproject.ui.base.OnForwardListener
import com.summer.itis.cardsproject.ui.base.OnOkListener
import com.summer.itis.cardsproject.ui.tests.add_test.fragments.main.AddTestFragment
import com.summer.itis.cardsproject.ui.tests.test_item.fragments.main.TestFragment
import com.summer.itis.cardsproject.utils.Const.gsonConverter
import kotlinx.android.synthetic.main.fragment_test.*
import com.summer.itis.cardsproject.ui.tests.ChangeToolbarListener
import com.summer.itis.cardsproject.ui.tests.add_test.fragments.question.AddQuestionFragment
import com.summer.itis.cardsproject.ui.tests.test_list.test.TestListActivity
import com.summer.itis.cardsproject.utils.Const
import com.summer.itis.cardsproject.utils.Const.EDIT_STATUS
import com.summer.itis.cardsproject.utils.Const.ONLINE_STATUS
import kotlinx.android.synthetic.main.back_forward.*


class TestActivity : BaseBackActivity(), TestView, ChangeToolbarListener {

    internal var PLACE_PICKER_REQUEST = 1

    @InjectPresenter
    lateinit var presenter: TestPresenter

    lateinit var test: Test

    private val containerId: Int
        get() = R.id.fragment_container

    protected val fragment: Fragment
        get() = AddTestFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatus(EDIT_STATUS)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_frame_and_toolbar)
        initViews()

        val testStr: String = intent.getStringExtra(TEST_JSON)
        val args: Bundle = Bundle()
        args.putString(TEST_JSON,testStr)
        val fragment = TestFragment.newInstance(args)
//        changeFragment(fragment)
//        val fragmentManager = supportFragmentManager
        if (supportFragmentManager.findFragmentById(containerId) == null) {
            supportFragmentManager.beginTransaction()
                    .add(containerId, fragment, TEST_FRAGMENT)
                    .commit()
        }

    }

    override fun onTestBackPressed() {

    }

    override fun changeToolbar(tag: String, title: String) {
        setToolbarTitle(title)
        when {
            TEST_FRAGMENT.equals(tag) -> {
                btn_ok.visibility = View.GONE
                btn_cancel.visibility = View.GONE
                btn_forward.visibility = View.GONE
            }

            QUESTION_FRAGMENT.equals(tag) -> {
                btn_back.visibility = View.GONE
                btn_cancel.visibility = View.VISIBLE
                btn_forward.visibility = View.VISIBLE
            }

            FINISH_FRAGMENT.equals(tag) -> {
                btn_ok.visibility = View.VISIBLE
                btn_cancel.visibility = View.GONE
                btn_back.visibility = View.GONE
                btn_forward.visibility = View.GONE
            }

            ANSWERS_FRAGMENT.equals(tag) -> {
                btn_ok.visibility = View.GONE
                btn_cancel.visibility = View.VISIBLE
                btn_back.visibility = View.VISIBLE
                btn_forward.visibility = View.VISIBLE
            }

            WINNED_FRAGMENT.equals(tag) -> {
                btn_back.visibility = View.VISIBLE
                btn_cancel.visibility = View.GONE
                btn_forward.visibility = View.GONE
                btn_ok.visibility = View.GONE
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
        setSupportActionBar(test_toolbar)
        btn_back.setOnClickListener(this)
        btn_forward.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
        btn_ok.setOnClickListener(this)
//        setBackArrow(toolbar)

    }

    companion object {

        const val TEST_JSON: String = "test_json"

        const val QUESTION_FRAGMENT: String = "question_fragment"
        const val TEST_FRAGMENT: String = "test_fragment"
        const val ANSWERS_FRAGMENT: String = "answers_fragment"
        const val FINISH_FRAGMENT: String = "finish_fragment"
        const val WINNED_FRAGMENT: String = "winned_fragment"


        fun start(activity: Activity, test: Test) {
            val intent = Intent(activity, TestActivity::class.java)
            val testStr: String = gsonConverter.toJson(test)
            intent.putExtra(TEST_JSON,testStr)
            activity.startActivity(intent)
        }
    }

}
