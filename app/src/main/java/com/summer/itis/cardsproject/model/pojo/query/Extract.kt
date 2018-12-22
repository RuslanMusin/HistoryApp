package com.summer.itis.summerproject.model.pojo.query

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Text

class Extract {

    @field:Text var content: String? = null

    @field:Attribute(required = false) var space: String? = null

    override fun toString(): String {
        return "ClassPojo [content = $content]"
    }
}
