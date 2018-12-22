package com.summer.itis.cardsproject.ui.tests

import android.view.View

interface ChangeToolbarListener: View.OnClickListener {

    fun changeToolbar(tag: String, title: String)

    fun showOk(boolean: Boolean)

    fun setToolbarTitle(title: String)
}
