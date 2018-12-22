package com.summer.itis.cardsproject.model.pojo.opensearch

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root
class Image {
    @field:Attribute(name="height") var height: String? = null

    @field:Attribute(name="source") var source: String? = null

    @field:Attribute(name="width") var width: String? = null

    override fun toString(): String {
        return "ClassPojo [height = $height, source = $source, width = $width]"
    }
}
