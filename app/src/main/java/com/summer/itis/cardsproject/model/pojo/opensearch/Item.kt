package com.summer.itis.cardsproject.model.pojo.opensearch


import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "Item")
class Item {
    @field:Element(name = "Text") var text: Text? = null

    @field:Element(name = "Url") var url: Url? = null

    @field:Element(name = "Description", required = false) var description: Description? = null

    @field:Element(name = "Image", required = false) var image: Image? = null

    @field:Attribute(required = false) var space: String? = null
}

