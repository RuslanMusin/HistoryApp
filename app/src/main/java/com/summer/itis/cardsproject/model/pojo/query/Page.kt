package com.summer.itis.summerproject.model.pojo.query


import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root
class Page {

    @field:Attribute(required = false) var ns: String? = null

    @field:Attribute(name="title") var title: String? = null

    @field:Element(required = false) var original: Original? = null

    @field:Attribute var description: String? = null

    @field:Attribute(required = false) var _idx: String? = null

    @field:Attribute(required = false) var descriptionsource: String? = null

    @field:Element var extract: Extract? = null

    @field:Attribute(required = false) var pageid: String? = null

    @field:Attribute(required = false) var space: String? = null
}

