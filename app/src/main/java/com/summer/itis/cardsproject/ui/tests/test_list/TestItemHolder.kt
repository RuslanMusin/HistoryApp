package com.summer.itis.summerproject.ui.tests.test_list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import com.bumptech.glide.Glide

import com.summer.itis.summerproject.R
import com.summer.itis.summerproject.model.Test
import kotlinx.android.synthetic.main.item_member.view.*

class TestItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: Test) {
        itemView.tv_name.text = item.title

        itemView.tv_description.text = item.desc?.let { cutLongDescription(it) }

        if (item.imageUrl != null) {
            Glide.with(itemView.iv_cover.context)
                    .load(item.imageUrl)
                    .into(itemView.iv_cover)

        }
    }

    private fun cutLongDescription(description: String): String {
        return if (description.length < MAX_LENGTH) {
            description
        } else {
            description.substring(0, MAX_LENGTH - MORE_TEXT.length) + MORE_TEXT
        }
    }

    companion object {

        private val MAX_LENGTH = 80
        private val MORE_TEXT = "..."

        fun create(parent: ViewGroup): TestItemHolder {
            val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_member, parent, false);
//            val view = View.inflate(context, R.layout.item_member, null )
            val holder = TestItemHolder(view)
            return holder
        }
    }
}
