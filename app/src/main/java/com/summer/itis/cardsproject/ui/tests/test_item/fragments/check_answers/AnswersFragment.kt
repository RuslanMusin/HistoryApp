package com.summer.itis.summerproject.ui.tests.test_item.fragments.check_answers

import QuestionFragment.Companion.ANSWERS_TYPE
import QuestionFragment.Companion.RIGHT_ANSWERS
import QuestionFragment.Companion.WRONG_ANSWERS
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.CompoundButtonCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import com.summer.itis.summerproject.R
import com.summer.itis.summerproject.R.string.answer
import com.summer.itis.summerproject.model.Answer
import com.summer.itis.summerproject.model.Question
import com.summer.itis.summerproject.model.Test
import com.summer.itis.summerproject.repository.RepositoryProvider.Companion.userRepository
import com.summer.itis.summerproject.ui.base.BaseBackActivity
import com.summer.itis.summerproject.ui.base.OnBackPressedListener
import com.summer.itis.summerproject.ui.base.OnFourActionListener
import com.summer.itis.summerproject.ui.tests.ChangeToolbarListener
import com.summer.itis.summerproject.ui.tests.test_item.TestActivity
import com.summer.itis.summerproject.ui.tests.test_item.TestActivity.Companion.ANSWERS_FRAGMENT
import com.summer.itis.summerproject.ui.tests.test_item.TestActivity.Companion.FINISH_FRAGMENT
import com.summer.itis.summerproject.ui.tests.test_item.TestActivity.Companion.QUESTION_FRAGMENT
import com.summer.itis.summerproject.ui.tests.test_item.TestActivity.Companion.TEST_JSON
import com.summer.itis.summerproject.ui.tests.test_item.fragments.finish.FinishFragment
import com.summer.itis.summerproject.ui.tests.test_item.fragments.main.TestFragment
import com.summer.itis.summerproject.utils.Const
import com.summer.itis.summerproject.utils.Const.ONLINE_STATUS
import com.summer.itis.summerproject.utils.Const.TAG_LOG
import com.summer.itis.summerproject.utils.Const.gsonConverter
import kotlinx.android.synthetic.main.fragment_question.*
import java.util.ArrayList

class AnswersFragment : Fragment(), View.OnClickListener, OnFourActionListener {

    private lateinit var question: Question
    private lateinit var test: Test
    private lateinit var type: String
    private var listSize: Int = 0
    private var number: Int = 0

    private lateinit var colorStateList: ColorStateList
    private lateinit var rightStateList: ColorStateList

    private var textViews: MutableList<TextView>? = null
    private var checkBoxes: MutableList<CheckBox>? = null
    private var radioButtons: MutableList<RadioButton>? = null

    override fun onBackPressed() {
        beforeQuestion()
    }

    override fun onCancel() {
        finishQuestions()
    }

    override fun onForward() {
        nextQuestion()
    }

    override fun onOk() {
        finishQuestions()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_question, container, false)

        type = arguments?.getString(ANSWERS_TYPE)!!
        val testStr: String = arguments?.getString(TEST_JSON)!!
        number = arguments?.getInt(QUESTION_NUMBER)!!
        test = gsonConverter.fromJson(testStr, Test::class.java)
        if(type.equals(RIGHT_ANSWERS)) {
            question =  test.rightQuestions[number]
            listSize = test.rightQuestions.size
        } else {
           question = test.wrongQuestions[number]
            listSize = test.wrongQuestions.size

        }

        (activity as BaseBackActivity).currentTag = TestActivity.ANSWERS_FRAGMENT + number
        (activity as ChangeToolbarListener).changeToolbar(ANSWERS_FRAGMENT,"Вопрос ${number+1}/${listSize}")
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)
        setListeners()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initViews(view: View) {
        textViews = ArrayList()
        radioButtons = ArrayList()
        checkBoxes = ArrayList()

        if(number == (listSize-1)) {
            btn_next_question.visibility = View.GONE
            btn_finish_questions.visibility = View.VISIBLE
            (activity as ChangeToolbarListener).showOk(true)
        }

        tv_question.text = question.question

        setStartAnswers()
    }

    private fun setStartAnswers() {
        colorStateList = ColorStateList(
                arrayOf(intArrayOf(-android.R.attr.state_checked), // unchecked
                        intArrayOf(android.R.attr.state_checked))// checked
                ,
                intArrayOf(Color.parseColor("#FFFFFF"), Color.parseColor("#DC143C"))
        )

        rightStateList = ColorStateList(
                arrayOf(intArrayOf(-android.R.attr.state_checked), // unchecked
                        intArrayOf(android.R.attr.state_checked))// checked
                ,
                intArrayOf(Color.parseColor("#FFFFFF"), Color.parseColor("#00cc00"))
        )

        for (answer in question.answers) {
            addAnswer(answer)
        }
        for(tv in textViews!!) {
            Log.d(Const.TAG_LOG,"text = " + tv.text)
        }


    }


    private fun setListeners() {
        btn_finish_questions!!.setOnClickListener(this)
        btn_next_question!!.setOnClickListener(this)

        btn_next_question.text = getString(R.string.next_question)
    }

    private fun beforeQuestion() {
        if(number > 0) {
            val args: Bundle = Bundle()
            args.putString(TEST_JSON, gsonConverter.toJson(test))
            args.putString(ANSWERS_TYPE, type)
            args.putInt(QUESTION_NUMBER, --number)
            val fragment = AnswersFragment.newInstance(args)
            (activity as BaseBackActivity).changeFragment(fragment, ANSWERS_FRAGMENT + number)
        } else {
            finishQuestions()
        }
    }

    private fun finishQuestions() {
        val args: Bundle = Bundle()
        args.putString(TEST_JSON, gsonConverter.toJson(test))
        val fragment = FinishFragment.newInstance(args)
        (activity as BaseBackActivity).changeFragment(fragment, FINISH_FRAGMENT)    }

    private fun nextQuestion() {
        val args: Bundle = Bundle()
        args.putString(TEST_JSON, gsonConverter.toJson(test))
        args.putString(ANSWERS_TYPE,type)
        args.putInt(QUESTION_NUMBER, ++number)
        val fragment = AnswersFragment.newInstance(args)
        (activity as BaseBackActivity).changeFragment(fragment, ANSWERS_FRAGMENT + number)
    }

    override fun onClick(v: View) {


        when (v.id) {

            R.id.btn_finish_questions -> {
                finishQuestions()
               /* val args: Bundle = Bundle()
                args.putString(TEST_JSON, gsonConverter.toJson(test))
                activity!!.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, TestFragment.newInstance(args))
                        .addToBackStack("AddQuestionFragment")
                        .commit()        */
            }

            R.id.btn_next_question -> {
                nextQuestion()
              /*  val args: Bundle = Bundle()
                args.putString(TEST_JSON, gsonConverter.toJson(test))
                args.putInt(QUESTION_NUMBER, ++number)
                args.putString(ANSWERS_TYPE, type)
                activity!!.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, AnswersFragment.newInstance(args))
                        .addToBackStack("AddQuestionFragment")
                        .commit()*/
            }

        }
    }

    private fun addAnswer(answer: Answer) {
        val view: LinearLayout = layoutInflater.inflate(R.layout.layout_item_question,li_answers,false) as LinearLayout
        val tvAnswer: TextView = view.findViewWithTag("tv_answer")
        tvAnswer.text = answer.text
        textViews?.add(tvAnswer)
        val checkBox: CheckBox = view.findViewWithTag("checkbox")
        if(answer.isRight) {
            CompoundButtonCompat.setButtonTintList(checkBox, rightStateList)
            checkBox.isChecked = true
        }
        checkBoxes?.add(checkBox)
        li_answers.addView(view)
        if(type.equals(WRONG_ANSWERS) && !answer.isRight && answer.userClicked != answer.isRight) {
            Log.d(TAG_LOG,"change checkbox color")
            Log.d(Const.TAG_LOG,"text tv = ${tvAnswer.text}")
            Log.d(TAG_LOG,"answer.isRight = ${answer.isRight} and userClick = ${answer.userClicked}")
            CompoundButtonCompat.setButtonTintList(checkBox, colorStateList)
            checkBox.isChecked = true
        }
        checkBox.isEnabled = false
    }

    companion object {

        private val RESULT_LOAD_IMG = 0

        const val QUESTION_NUMBER = "queston_number"

        const val RIGHT_ANSWERS = "right_answers"
        const val WRONG_ANSWERS = "wrong_answers"
        const val ANSWERS_TYPE = "type_answers"
        const val CARD_JSON = "card_json"


        fun newInstance(args: Bundle): Fragment {
            val fragment = AnswersFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
