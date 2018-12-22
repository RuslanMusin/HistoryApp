package com.summer.itis.summerproject.ui.base

import android.support.v4.app.Fragment
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.MvpAppCompatFragment
import com.summer.itis.summerproject.R
import com.summer.itis.summerproject.ui.tests.test_item.fragments.finish.FinishFragment

abstract class BaseBackActivity: NavigationBaseActivity() {

    lateinit var currentTag: String

    override fun onBackPressed() {
        (getCurrentFragment() as OnBackPressedListener).onBackPressed()
        super.onBackPressed()
    }

    fun changeFragment(fragment: Fragment,tag: String) {
        supportFragmentManager
                .beginTransaction()
                .remove(getCurrentFragment())
                .add(R.id.fragment_container, fragment,tag)
                .commit()
    }

    fun getCurrentFragment(): Fragment {
        return supportFragmentManager.findFragmentByTag(currentTag)
    }

}
