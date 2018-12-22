package com.summer.itis.summerproject.model.pojo.query

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element

class Original {

    @field:Attribute(name="height") var height: String? = null

    @field:Attribute(name="source") var source: String? = null

    @field:Attribute(name="width") var width: String? = null

    override fun toString(): String {
        return "ClassPojo [height = $height, source = $source, width = $width]"
    }
}
