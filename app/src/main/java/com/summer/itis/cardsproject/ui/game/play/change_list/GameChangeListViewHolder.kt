package com.summer.itis.summerproject.ui.game.play.change_list

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.summer.itis.summerproject.R
import com.summer.itis.summerproject.model.Card
import android.view.animation.AnimationUtils
import com.summer.itis.summerproject.ui.game.play.PlayGameActivity.Companion.setWeight
import com.summer.itis.summerproject.utils.Const.TAG_LOG
import kotlinx.android.synthetic.main.item_game_card_medium.view.*


class GameChangeListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(card: Card) {
        itemView.tv_card_person_name.text = card.abstractCard?.name

        card.abstractCard?.photoUrl?.let {
            Glide.with(itemView.iv_card.context)
                    .load(it)
                    .into(itemView.iv_card)

        }

        setWeight(itemView.ll_card_params.view_card_intelligence, card.intelligence!!.toFloat())
        setWeight(itemView.ll_card_params.view_card_support, card.support!!.toFloat())
        setWeight(itemView.ll_card_params.view_card_prestige, card.prestige!!.toFloat())
        setWeight(itemView.ll_card_params.view_card_hp, card.hp!!.toFloat())
        setWeight(itemView.ll_card_params.view_card_strength, card.strength!!.toFloat())
    }

    private fun cutLongDescription(description: String): String {
        return if (description.length < MAX_LENGTH) {
            description
        } else {
            description.substring(0, MAX_LENGTH - MORE_TEXT.length) + MORE_TEXT
        }
    }

    companion object  {


        private val MAX_LENGTH = 80
        private val MORE_TEXT = "..."

        fun create(parent: ViewGroup): GameChangeListViewHolder {
            val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_game_card_medium, parent, false);
            val holder = GameChangeListViewHolder(view)

            setFocusOnView(view)

            return holder
        }

        private fun setFocusOnView(view: View?) {
            view?.setOnFocusChangeListener({ v, hasFocus ->
                Log.d(TAG_LOG,"changed anim focus = $hasFocus")
                if (hasFocus) {
                    // run scale animation and make it bigger
                    val anim = AnimationUtils.loadAnimation(view.context, R.anim.scale_in_tv)
                    view.startAnimation(anim)
                    anim.fillAfter = true
                } else {
                    // run scale animation and make it smaller
                    val anim = AnimationUtils.loadAnimation(view.context, R.anim.scale_out_tv)
                    view.startAnimation(anim)
                    anim.fillAfter = true
                }
            })
        }
    }
}
