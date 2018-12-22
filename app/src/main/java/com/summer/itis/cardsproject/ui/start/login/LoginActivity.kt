package com.summer.itis.summerproject.ui.start.login


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.summer.itis.summerproject.R
import com.summer.itis.summerproject.R.string.password
import com.summer.itis.summerproject.R.string.username
import com.summer.itis.summerproject.ui.base.BaseActivity
import com.summer.itis.summerproject.ui.member.member_item.PersonalActivity
import com.summer.itis.summerproject.ui.start.registration.RegistrationActivity
import com.summer.itis.summerproject.utils.Const.USER_DATA_PREFERENCES
import com.summer.itis.summerproject.utils.Const.USER_PASSWORD
import com.summer.itis.summerproject.utils.Const.USER_USERNAME
import kotlinx.android.synthetic.main.activity_login.*

/**
 * Created by Ruslan on 18.02.2018.
 */

class LoginActivity : BaseActivity(), View.OnClickListener {

    private var enterBtn: Button? = null
    private var tvRegistration: TextView? = null
    var tiUsername: TextInputLayout? = null
    var tiPassword: TextInputLayout? = null
    var etUsername: EditText? = null
    var etPassword: EditText? = null

    var fireAuth: FirebaseAuth? = null

    private var presenter: LoginPresenter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = LoginPresenter(this)
        initViews()
        fireAuth = FirebaseAuth.getInstance()
        checkUserSession()


    }

    private fun checkUserSession() {
        val sharedPreferences: SharedPreferences = getSharedPreferences(USER_DATA_PREFERENCES,Context.MODE_PRIVATE)
        if(sharedPreferences.contains(USER_USERNAME)) {
            val email: String = sharedPreferences.getString(USER_USERNAME,"")
            val passwored: String = sharedPreferences.getString(USER_PASSWORD,"")
            presenter?.signIn(email,passwored)
        }
    }

    private fun initViews() {
        setContentView(R.layout.activity_login)

        enterBtn = findViewById(R.id.btn_enter)
        tvRegistration = findViewById(R.id.link_signup)

        enterBtn!!.setOnClickListener(this)
        tvRegistration!!.setOnClickListener(this)

        etUsername = findViewById(R.id.et_name)
        etPassword = findViewById(R.id.et_password)
        tiUsername = findViewById(R.id.ti_username)
        tiPassword = findViewById(R.id.ti_password)


        iv_cover.setOnClickListener(this)
        tv_name.setOnClickListener(this)

//        enterBtn!!.performClick()
    }

    override fun onClick(view: View) {

        when (view.id) {

            R.id.btn_enter -> {
                val username = etUsername?.getText().toString();
                val password = etPassword?.getText().toString();
             /*   val username = "rust@ma.ru"
                val password = "rustamka"*/
                presenter!!.signIn(username, password)
            }

            R.id.link_signup -> goToRegistration()

            R.id.tv_name -> {
                etUsername?.setText("rast@ma.ru")
                etPassword?.setText("rastamka")

            }

            R.id.iv_cover -> {
                etUsername?.setText("rust@ma.ru")
                etPassword?.setText("rustamka")
            }
        }
    }

    internal fun goToProfile() {
        PersonalActivity.start(this)
    }

    private fun goToRegistration() {
        RegistrationActivity.start(this)
    }

    fun showError() {
        tiUsername!!.error = getString(R.string.enter_correct_name)
        tiPassword!!.error = getString(R.string.enter_correct_password)
    }

    companion object {

        fun start(activity: Context) {
            val intent = Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            activity.startActivity(intent)
        }
    }
}
