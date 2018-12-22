package com.summer.itis.summerproject.ui.tests.add_test.fragments.main

import GameQuestionFragment.Companion.QUESTION_NUMBER
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.bumptech.glide.Glide

import com.summer.itis.summerproject.R
import com.summer.itis.summerproject.R.string.card
import com.summer.itis.summerproject.model.Card
import com.summer.itis.summerproject.model.Test
import com.summer.itis.summerproject.repository.RepositoryProvider.Companion.userRepository
import com.summer.itis.summerproject.ui.base.BaseBackActivity
import com.summer.itis.summerproject.ui.base.NavigationBaseActivity
import com.summer.itis.summerproject.ui.base.OnBackPressedListener
import com.summer.itis.summerproject.ui.cards.add_card.AddCardActivity.Companion.CARD_EXTRA
import com.summer.itis.summerproject.ui.cards.add_card_list.AddCardListActivity
import com.summer.itis.summerproject.ui.tests.ChangeToolbarListener
import com.summer.itis.summerproject.ui.tests.add_test.AddTestActivity.Companion.ADD_QUESTION_FRAGMENT
import com.summer.itis.summerproject.ui.tests.add_test.AddTestActivity.Companion.ADD_TEST_FRAGMENT
import com.summer.itis.summerproject.ui.tests.add_test.AddTestView
import com.summer.itis.summerproject.ui.tests.add_test.fragments.main.AddTestFragment.Companion.ADD_CARD
import com.summer.itis.summerproject.ui.tests.add_test.fragments.question.AddQuestionFragment
import com.summer.itis.summerproject.ui.tests.test_item.TestActivity
import com.summer.itis.summerproject.ui.tests.test_item.TestActivity.Companion.ANSWERS_FRAGMENT
import com.summer.itis.summerproject.ui.tests.test_item.TestActivity.Companion.TEST_JSON
import com.summer.itis.summerproject.ui.tests.test_item.fragments.check_answers.AnswersFragment
import com.summer.itis.summerproject.ui.tests.test_item.fragments.finish.FinishFragment
import com.summer.itis.summerproject.ui.tests.test_item.fragments.main.TestFragment
import com.summer.itis.summerproject.ui.tests.test_list.test.TestListActivity

import com.summer.itis.summerproject.utils.Const.COMA
import com.summer.itis.summerproject.utils.Const.ONLINE_STATUS
import com.summer.itis.summerproject.utils.Const.TAG_LOG
import com.summer.itis.summerproject.utils.Const.gsonConverter
import kotlinx.android.synthetic.main.fragment_add_test.*
import kotlinx.android.synthetic.main.item_game_card_medium.*

class AddTestFragment : Fragment(), View.OnClickListener, OnBackPressedListener {

    private var imageUri: Uri? = null

    private var tiTestName: TextInputLayout? = null
    private var tiTestDesc: TextInputLayout? = null
    private var btnAddCard: Button? = null
    private var tvAddedCards: TextView? = null
    private var btnCreateQuestions: Button? = null

    private var etTestName: EditText? = null
    private var etTestDesc: EditText? = null

    private var addTestView: AddTestView? = null
    private var test: Test? = null

    companion object {

        const val ADD_CARD: Int = 1

        fun newInstance(args: Bundle): Fragment {
            val fragment = AddTestFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): Fragment {
            val fragment = AddTestFragment()
            return fragment
        }
    }

    override fun onBackPressed() {
        TestListActivity.start(activity as Activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_test, container, false)
        (activity as BaseBackActivity).currentTag = ADD_TEST_FRAGMENT
        (activity as ChangeToolbarListener).changeToolbar(ADD_TEST_FRAGMENT,"Добавить тест")
//        addTestView = activity as AddTestView?

        return view
    }

    private fun setTestData() {
        etTestName!!.setText(test?.title)
        etTestDesc!!.setText(test?.desc)
        tvAddedCards!!.text =test?.card?.abstractCard?.name
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)
        setListeners()

        if(arguments == null) {
            test = Test()
        } else {
            test = gsonConverter.fromJson(arguments?.getString(TEST_JSON),Test::class.java)
            setTestData()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initViews(view: View) {
        etTestDesc = view.findViewById(R.id.et_test_desc)
        etTestName = view.findViewById(R.id.et_test_name)
        tiTestName = view.findViewById(R.id.ti_test_name)
        tiTestDesc = view.findViewById(R.id.ti_test_desc)
        btnAddCard = view.findViewById(R.id.btn_add_card)
        btnCreateQuestions = view.findViewById(R.id.btn_create_questions)
        tvAddedCards = view.findViewById(R.id.tv_added_cards)
    }

    private fun setListeners() {
        btnCreateQuestions!!.setOnClickListener(this)
        btnAddCard!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.btn_create_questions -> {
                test!!.title = etTestName!!.text.toString()
                test!!.desc = etTestDesc!!.text.toString()

//                addTestView!!.setTest(test!!)

                if(checkTest()) {//
                    val args: Bundle = Bundle()
                    args.putString(TEST_JSON, gsonConverter.toJson(test))
                    args.putInt(QUESTION_NUMBER, 0)
                    val fragment = AddQuestionFragment.newInstance(args)
                    (activity as BaseBackActivity).changeFragment(fragment, ADD_QUESTION_FRAGMENT + 0)
                } else {

                }
            }

            R.id.btn_add_card -> {
                val intent = Intent(activity, AddCardListActivity::class.java)
                startActivityForResult(intent, ADD_CARD)

            }
        }
    }

    private fun checkTest(): Boolean {
        var flag: Boolean = if(test == null) false else true
        test?.let {
            if(it.title == null  || it.title?.trim().equals("")) {
                tiTestName?.error = "Введите название теста!"
                flag = false
            } else {
                tiTestName?.error = null
            }
            if(it.desc == null  || it.desc?.trim().equals("")) {
                tiTestDesc?.error = "Введите описание теста!"

                flag = false

            } else {
                tiTestDesc?.error = null
            }
            if(it.card == null) {
                tv_test_card_name.requestFocus();
                tv_test_card_name.setError("Добавьте карту!");
                flag = false
            } else {
                tv_test_card_name.setError(null);
            }
        }
        Log.d(TAG_LOG, "flag = $flag")
        return flag
    }
    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)

        if (reqCode == ADD_CARD && resultCode == Activity.RESULT_OK) {
            val card = gsonConverter.fromJson(data!!.getStringExtra(CARD_EXTRA), Card::class.java)
            tvAddedCards!!.text = card.abstractCard.name
            test!!.card = card
            Glide.with(this)
                    .load(card.abstractCard.photoUrl)
                    .into(iv_cover)
            tv_test_card_name.setError(null);
        }
    }
}
