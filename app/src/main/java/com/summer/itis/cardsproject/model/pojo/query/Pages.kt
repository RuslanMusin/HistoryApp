package com.summer.itis.cardsproject.model.pojo.query

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList

class Pages {

    @field: ElementList(inline = true, required = false) var pages: List<Page>? = null
}
