package com.summer.itis.summerproject.model.pojo.query

import org.simpleframework.xml.Element

class Query {

    @field:Element var pages: Pages? = null

    override fun toString(): String {
        return "ClassPojo [pages = $pages]"
    }
}
