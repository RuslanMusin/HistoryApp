package com.summer.itis.summerproject.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.summer.itis.summerproject.model.AbstractCard

@IgnoreExtraProperties
open class Card() : Parcelable{

    var id: String? = null

    var cardId: String? = null

    var testId: String? = null

    var intelligence: Int? = null

    var support: Int? = null

    var prestige: Int? = null

    var hp: Int? = null

    var strength: Int? = null

    var type: String? = null

    @Exclude
    var abstractCard: AbstractCard = AbstractCard()

    @Exclude
    var test: Test = Test()

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        cardId = parcel.readString()
        testId = parcel.readString()
        intelligence = parcel.readValue(Int::class.java.classLoader) as? Int
        support = parcel.readValue(Int::class.java.classLoader) as? Int
        prestige = parcel.readValue(Int::class.java.classLoader) as? Int
        hp = parcel.readValue(Int::class.java.classLoader) as? Int
        strength = parcel.readValue(Int::class.java.classLoader) as? Int
        abstractCard = parcel.readParcelable(AbstractCard::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(cardId)
        parcel.writeString(testId)
        parcel.writeValue(intelligence)
        parcel.writeValue(support)
        parcel.writeValue(prestige)
        parcel.writeValue(hp)
        parcel.writeValue(strength)
        parcel.writeParcelable(abstractCard, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<Card> {
        override fun createFromParcel(parcel: Parcel): Card {
            return Card(parcel)
        }

        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }
}
