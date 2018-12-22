package com.summer.itis.summerproject.ui.game.play

import com.arellomobile.mvp.MvpView

interface PlayView: MvpView {

    fun onAnswer(isRight: Boolean)
}