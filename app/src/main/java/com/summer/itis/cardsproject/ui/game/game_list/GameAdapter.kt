package com.summer.itis.cardsproject.ui.game.game_list

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.Test
import com.summer.itis.cardsproject.model.game.Lobby
import com.summer.itis.cardsproject.model.game.LobbyPlayerData
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.gamesRepository
import com.summer.itis.cardsproject.ui.base.BaseAdapter
import com.summer.itis.cardsproject.ui.game.add_game.AddGameActivity
import com.summer.itis.cardsproject.ui.tests.test_list.TestItemHolder
import com.summer.itis.cardsproject.utils.ApplicationHelper
import kotlinx.android.synthetic.main.item_game.view.*

class GameAdapter(items: MutableList<Lobby>) : BaseAdapter<Lobby, GameItemHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameItemHolder {
//        return TestItemHolder.create(parent.context)
        return GameItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: GameItemHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        holder.bind(item)

        if(item.isMyCreation) {
            holder.itemView.btn_delete.visibility = View.VISIBLE

            holder.itemView.btn_delete.setOnClickListener{
                MaterialDialog.Builder(holder.itemView.context)
                        .title(R.string.delete_game)
                        .content(R.string.game_will_be_deleted)
                        .positiveText("Удалить")
                        .onPositive(object :MaterialDialog.SingleButtonCallback {
                            override fun onClick(dialog: MaterialDialog, which: DialogAction) {
                                removeItem(item)
                            }

                        })
                        .negativeText("Отмена")
                        .onNegative{ dialog, action -> dialog.cancel()}
                        .show()
            }
        }

    }

    fun removeItemById(id: String?) {
        id?.let {
            for(item in items) {
                if(item.id.equals(it)) {
                    removeItem(item)
                }
            }
        }
    }

    private fun removeItem(item: Lobby) {
        val pos = items.indexOf(item)
        gamesRepository.removeLobby(item.id)
        items.removeAt(pos)
        notifyItemRemoved(pos)
    }
}