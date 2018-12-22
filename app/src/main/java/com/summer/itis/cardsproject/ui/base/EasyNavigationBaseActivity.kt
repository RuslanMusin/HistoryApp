package com.summer.itis.summerproject.ui.base

import android.os.Bundle
import android.widget.FrameLayout
import com.summer.itis.summerproject.R

abstract class EasyNavigationBaseActivity : NavigationBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.container)
        layoutInflater.inflate(getContentLayout(), contentFrameLayout)

        supportActionBar(findViewById(R.id.toolbar))
    }

    abstract fun getContentLayout(): Int

}