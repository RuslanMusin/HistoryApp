import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.Answer
import com.summer.itis.cardsproject.model.Question
import com.summer.itis.cardsproject.ui.game.play.PlayGameActivity
import com.summer.itis.cardsproject.ui.game.play.PlayView
import com.summer.itis.cardsproject.utils.Const.gsonConverter
import kotlinx.android.synthetic.main.fragment_question.*
import java.util.*

class GameQuestionFragment : Fragment(), View.OnClickListener {

    private lateinit var question: Question

    private var textViews: MutableList<TextView>? = null
    private var checkBoxes: MutableList<CheckBox>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_question, container, false)

        question = gsonConverter.fromJson(arguments?.getString(QUESTION_JSON)!!, Question::class.java)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)
        setListeners()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initViews(view: View) {
        textViews = ArrayList()
        checkBoxes = ArrayList()

        tv_question.text = question.question

        setStartAnswers()
    }

    private fun setStartAnswers() {
        for (answer in question.answers) {
            addAnswer(answer)
        }
    }


    private fun setListeners() {
        btn_next_question!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_next_question -> {
                checkAnswers()
                (activity as PlayView).onAnswer(question.userRight)
            }
        }
    }

    private fun checkAnswers() {
        question.userRight = true
        for (i in question.answers.indices) {
            val answer: Answer = question.answers[i]
            if (checkBoxes?.get(i)?.isChecked!!) {
                answer.userClicked = true
            }
            if (answer.isRight && answer.userClicked != answer.isRight) {
                question.userRight = false
            }
        }
    }

    private fun addAnswer(answer: Answer) {
        val view: LinearLayout = layoutInflater.inflate(R.layout.layout_item_question, li_answers, false) as LinearLayout
        val tvAnswer: TextView = view.findViewWithTag("tv_answer")
        tvAnswer.text = answer.text
        textViews?.add(tvAnswer)
        val checkBox: CheckBox = view.findViewWithTag("checkbox")
        checkBoxes?.add(checkBox)
        li_answers.addView(view)
    }

    companion object {

        const val QUESTION_JSON = "queston_json"

        //нужно где-то в другой части приложения...
        const val QUESTION_NUMBER = "queston_number"

        fun newInstance(question: Question): Fragment {
            val fragment = GameQuestionFragment()
            val args: Bundle = Bundle()
            args.putString(QUESTION_JSON, gsonConverter.toJson(question))
            fragment.arguments = args
            return fragment
        }
    }
}
