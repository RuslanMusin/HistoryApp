package com.summer.itis.cardsproject.ui.game.add_photo

import android.view.ViewGroup
import com.summer.itis.cardsproject.model.db_dop_models.PhotoItem
import com.summer.itis.cardsproject.ui.base.BaseAdapter
import com.summer.itis.cardsproject.ui.cards.add_card_list.AddCardListHolder

class AddPhotoListAdapter(items: MutableList<PhotoItem>) : BaseAdapter<PhotoItem, AddPhotoListHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddPhotoListHolder {
        return AddPhotoListHolder.create(parent.context)
    }

    override fun onBindViewHolder(holder: AddPhotoListHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        holder.bind(item)
    }
}
