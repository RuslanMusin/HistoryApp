package com.summer.itis.cardsproject.model.pojo.query

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "api")
class Api {

    @field:Element var query: Query? = null

    @field:Attribute(required = false) var batchcomplete: String? = null

    override fun toString(): String {
        return "ClassPojo [query = $query, batchcomplete = $batchcomplete]"
    }
}
