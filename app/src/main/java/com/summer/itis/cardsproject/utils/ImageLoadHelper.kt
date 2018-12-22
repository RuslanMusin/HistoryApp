package com.summer.itis.summerproject.utils

import android.support.annotation.DrawableRes
import android.widget.ImageView

import com.squareup.picasso.Picasso
import com.summer.itis.summerproject.R

//КЛАСС ДЛЯ РАБОТЫ С ФОТКАМИ.НЕ ИСПОЛЬЗОВАТЬ, ПЕРЕЖИТОК ПРОШЛОГО,ЮЗАТЬ GLIDE
object ImageLoadHelper {

    fun loadPicture(imageView: ImageView, url: String) {
        Picasso.with(imageView.context)
                .load(url)
                /* .placeholder(R.mipmap.ic_marvel_launcher)
                .error(R.mipmap.ic_marvel_launcher)*/
                .noFade()
                .into(imageView)
    }

    fun loadPictureByDrawable(imageView: ImageView, @DrawableRes drawable: Int) {
        Picasso.with(imageView.context)
                .load(drawable)
                .resize(1280, 720)
                .noFade()
                .into(imageView)
    }

    fun loadPictureByDrawableDefault(imageView: ImageView, @DrawableRes drawable: Int) {
        Picasso.with(imageView.context)
                .load(drawable)
                .noFade()
                .into(imageView)
    }
}
