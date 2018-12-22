package com.summer.itis.summerproject.model.pojo.opensearch

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import kotlin.text.Typography.section

class SearchSuggestion {
    @field:Element(name = "Section") var section: Section? = null

    @field:Element(name = "Query") var query: String? = null

    @field:Attribute(required = false) var xmlns: String? = null

    @field:Attribute(required = false) var version: String? = null

    override fun toString(): String {
        return "ClassPojo [Section = $section, Query = $query, xmlns = $xmlns, version = $version]"
    }
}
