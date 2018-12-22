package com.summer.itis.cardsproject.ui.game.play

import com.arellomobile.mvp.MvpView

interface PlayView: MvpView {

    fun onAnswer(isRight: Boolean)
}