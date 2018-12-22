package com.summer.itis.cardsproject.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class AbstractCard() : Parcelable{

    var id: String? = null

    var name: String? = null

    var lowerName: String? = null

    var photoUrl: String? = null

    var extract: String? = null

    var description: String? = null

    var wikiUrl: String? = null

    @field:Exclude var isOwner: Boolean = false

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        name = parcel.readString()
        photoUrl = parcel.readString()
        extract = parcel.readString()
        description = parcel.readString()
        wikiUrl = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(photoUrl)
        parcel.writeString(extract)
        parcel.writeString(description)
        parcel.writeString(wikiUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<AbstractCard> {
        override fun createFromParcel(parcel: Parcel): AbstractCard {
            return AbstractCard(parcel)
        }

        override fun newArray(size: Int): Array<AbstractCard?> {
            return arrayOfNulls(size)
        }
    }


}
