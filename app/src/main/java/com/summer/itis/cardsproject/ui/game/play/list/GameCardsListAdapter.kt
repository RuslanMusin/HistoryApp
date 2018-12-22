package com.summer.itis.cardsproject.ui.game.play.list

import android.content.Context
import android.support.constraint.Constraints
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.Card
import com.afollestad.materialdialogs.MaterialDialog
import com.ms.square.android.expandabletextview.ExpandableTextView
import com.summer.itis.cardsproject.R.id.expand_text_view
import kotlinx.android.synthetic.main.fragment_test_card.*


class GameCardsListAdapter(
        val items: ArrayList<Card>,
        val context: Context,
        val onClick: (card: Card) -> Unit) :
        RecyclerView.Adapter<GameCardsListViewHolder>() {

    var isClickable: Boolean = true

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameCardsListViewHolder {
        return GameCardsListViewHolder(
                LayoutInflater.from(context)
                        .inflate(R.layout.item_game_card_small, parent, false))
    }


    override fun onBindViewHolder(holder: GameCardsListViewHolder, position: Int) {
//        holder.nameView.text = items[position].abstractCard!!.name
        Glide.with(context)
                .load(items[position].abstractCard.photoUrl)
                .into(holder.image)

        holder.itemView.setOnClickListener {
            if(isClickable) {
                isClickable = false
                val pos = holder.adapterPosition

                removeElement(items[pos])
            }
        }

        holder.itemView.setOnLongClickListener{

            val dialog: MaterialDialog = MaterialDialog.Builder(it.context)
                    .customView(R.layout.fragment_test_card, false)
                    .build()

            val view: View? = dialog.customView
            view?.findViewById<ExpandableTextView>(R.id.expand_text_view)?.text = items[position].abstractCard.description
            view?.findViewById<TextView>(R.id.tv_name)?.text = items[position].abstractCard.name
            view?.findViewById<TextView>(R.id.tv_test_name)?.text = items[position].test.title

            view?.findViewById<ImageView>(R.id.iv_portrait)?.let { it1 ->
                Glide.with(it1.context)
                        .load(items[position].abstractCard.photoUrl)
                        .into(it1)
            }

            dialog.show()

            true
        }
    }

    fun changeDataSet(values: List<Card>) {
        items.clear()
        Log.d(Constraints.TAG, "values size = " + values.size)
        items.addAll(values)
        notifyDataSetChanged()
    }

    fun removeElement(card: Card) {
        val pos = items.indexOf(card)

        onClick(items[pos])

        //TODO delete selected in presenter?

        notifyItemRemoved(pos)
        items.removeAt(pos)
    }
}
