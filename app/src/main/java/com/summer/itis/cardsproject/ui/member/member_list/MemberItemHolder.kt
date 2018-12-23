package com.summer.itis.cardsproject.ui.member.member_list

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide

import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.User
import com.summer.itis.cardsproject.utils.AppHelper
import com.summer.itis.cardsproject.utils.Const.STUB_PATH

class MemberItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val name: TextView
    private val description: TextView
    private val imageView: ImageView

    init {
        name = itemView.findViewById(R.id.tv_name)
        description = itemView.findViewById(R.id.tv_description)
        imageView = itemView.findViewById(R.id.iv_cover)
    }

    fun bind(item: User) {
        name.text = item.username

        if (item.photoUrl != null) {
            if (item.photoUrl.equals(STUB_PATH)) {
//                ImageLoadHelper.loadPictureByDrawableDefault(imageView, R.drawable.ic_person_black_24dp)
                Glide.with(imageView.context)
                        .load(R.drawable.ic_account_circle_black_24dp)
                        .into(imageView)
            } else {
                //                ImageLoadHelper.loadPicture(imageView, items.getPhotoUrl());
                val imageReference = AppHelper.storageReference.child(item.photoUrl!!)

                Glide.with(imageView.context)
                        .load(imageReference)
                        .into(imageView)
            }

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

        fun create(context: Context): MemberItemHolder {
            val view = View.inflate(context, R.layout.item_member, null)
            val holder = MemberItemHolder(view)
            return holder
        }
    }
}
