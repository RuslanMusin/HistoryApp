package com.summer.itis.summerproject.ui.game.find

import com.arellomobile.mvp.MvpView

interface FindGameView : MvpView {
    fun showNothing()
    fun showNotSearching()
    fun showSearching()
    fun showNotEnoughCards()
    fun gameFinded(gameMode: String)
}
