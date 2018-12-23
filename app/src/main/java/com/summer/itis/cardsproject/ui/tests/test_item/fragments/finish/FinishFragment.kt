package com.summer.itis.cardsproject.ui.tests.test_item.fragments.finish

import QuestionFragment.Companion.ANSWERS_TYPE
import QuestionFragment.Companion.RIGHT_ANSWERS
import QuestionFragment.Companion.WRONG_ANSWERS
import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.Question
import com.summer.itis.cardsproject.model.Test
import com.summer.itis.cardsproject.repository.RepositoryProvider
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.testRepository
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.userEpochRepository
import com.summer.itis.cardsproject.ui.base.BaseBackActivity
import com.summer.itis.cardsproject.ui.base.OnBackPressedListener
import com.summer.itis.cardsproject.ui.base.OnOkListener
import com.summer.itis.cardsproject.ui.tests.ChangeToolbarListener
import com.summer.itis.cardsproject.ui.tests.test_item.TestActivity
import com.summer.itis.cardsproject.ui.tests.test_item.TestActivity.Companion.ANSWERS_FRAGMENT
import com.summer.itis.cardsproject.ui.tests.test_item.TestActivity.Companion.FINISH_FRAGMENT
import com.summer.itis.cardsproject.ui.tests.test_item.TestActivity.Companion.TEST_JSON
import com.summer.itis.cardsproject.ui.tests.test_item.TestActivity.Companion.WINNED_FRAGMENT
import com.summer.itis.cardsproject.ui.tests.test_item.fragments.check_answers.AnswersFragment
import com.summer.itis.cardsproject.ui.tests.test_item.fragments.winned_card.TestCardFragment
import com.summer.itis.cardsproject.utils.AppHelper
import com.summer.itis.cardsproject.utils.Const.TAG_LOG
import com.summer.itis.cardsproject.utils.Const.gsonConverter
import kotlinx.android.synthetic.main.fragment_finish_test.*

class FinishFragment : Fragment(), View.OnClickListener, OnBackPressedListener, OnOkListener {

    lateinit var test: Test
    var rightQuestions: MutableList<Question> = ArrayList()
    var wrongQuestions: MutableList<Question> = ArrayList()
    var procent: Long = 0

    override fun onBackPressed() {
       /* val args: Bundle = Bundle()
        args.putString(TEST_JSON, gsonConverter.toJson(test))
        val fragment = FinishFragment.newInstance(args)
        (activity as BaseBackActivity).changeFragment(fragment)*/
    }

    override fun onOk() {
        btn_finish_test.performClick()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_finish_test, container, false)
        (activity as BaseBackActivity).currentTag = TestActivity.FINISH_FRAGMENT
        (activity as ChangeToolbarListener).changeToolbar(FINISH_FRAGMENT,"Результат")

        test = gsonConverter.fromJson(arguments?.getString(TEST_JSON),Test::class.java)
        for(question in test.questions) {
            if(question.userRight) {
                rightQuestions.add(question)
            } else {
                wrongQuestions.add(question)
            }
        }
        test.rightQuestions = rightQuestions
        test.wrongQuestions = wrongQuestions

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tv_right_answers.text = rightQuestions.size.toString()
        tv_wrong_answers.text = wrongQuestions.size.toString()
        setCardText()

        btn_finish_test.setOnClickListener(this)
        li_wrong_answers.setOnClickListener(this)
        li_right_answers.setOnClickListener(this)
        li_winned_card.setOnClickListener(this)

        super.onViewCreated(view, savedInstanceState)
    }

    fun setCardText() {
        procent = Math.round((test.rightQuestions.size.toDouble() / test.questions.size.toDouble()) * 100)
        Log.d(TAG_LOG, "procent = $procent")
        if (procent >= 80) {
            Log.d(TAG_LOG, "finish it")
            tv_winned_card.text = test.card?.abstractCard?.name
            test.testDone = true
            AppHelper.currentUser?.let { testRepository.finishTest(test, it).subscribe() }

        } else {
            tv_winned_card.text = getText(R.string.test_failed)
        }
        userEpochRepository.updateAfterTest(AppHelper.currentUser.id, test).subscribe()
    }


    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.btn_finish_test -> {
                for(question in test.questions) {
                    question.userRight = false
                    for(answer in question.answers) {
                        answer.userClicked = false
                    }
                }
                TestActivity.start(activity as Activity,test)
            }

            R.id.li_wrong_answers -> {
                if(wrongQuestions.size > 0) {
                    prepareAnswers(WRONG_ANSWERS)
                }
            }

            R.id.li_right_answers -> {
                if(rightQuestions.size > 0) {
                    prepareAnswers(RIGHT_ANSWERS)
                }
            }

            R.id.li_winned_card -> {
                if(procent >= 80) {
                    val args: Bundle = Bundle()
                    args.putString(TEST_JSON, gsonConverter.toJson(test))
                    val fragment = TestCardFragment.newInstance(args)
                    (activity as BaseBackActivity).changeFragment(fragment, WINNED_FRAGMENT)
                }

            }
        }
    }

    fun prepareAnswers(type: String) {
        val args: Bundle = Bundle()
        args.putString(ANSWERS_TYPE, type)
        args.putString(TEST_JSON, gsonConverter.toJson(test))
        val fragment = AnswersFragment.newInstance(args)
        (activity as BaseBackActivity).changeFragment(fragment, ANSWERS_FRAGMENT + 0)
      /*  activity!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, AnswersFragment.newInstance(args))
                .addToBackStack("AddQuestionFragment")
                .commit()*/
    }


    companion object {

        fun newInstance(args: Bundle): Fragment {
            val fragment = FinishFragment()
            fragment.arguments = args
            return fragment
        }
    }
}