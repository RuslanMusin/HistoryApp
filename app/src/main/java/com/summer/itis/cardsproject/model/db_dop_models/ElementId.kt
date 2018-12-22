package com.summer.itis.summerproject.model.db_dop_models

import com.google.firebase.database.IgnoreExtraProperties

import java.util.HashMap

//КЛАСС ДЛЯ ТАБЛИЦ ID-ID
@IgnoreExtraProperties
open class ElementId : Identified {

    override lateinit var id: String

}
