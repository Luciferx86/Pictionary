package com.luciferx86.pictionary.Model

import android.os.Parcel
import android.os.Parcelable

class ChatPojo(var messageBody: String?, var messageFrom: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(messageBody)
        parcel.writeString(messageFrom)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "ChatPojo(messageBody=$messageBody, messageFrom=$messageFrom)"
    }

    companion object CREATOR : Parcelable.Creator<ChatPojo> {
        override fun createFromParcel(parcel: Parcel): ChatPojo {
            return ChatPojo(parcel)
        }

        override fun newArray(size: Int): Array<ChatPojo?> {
            return arrayOfNulls(size)
        }
    }


}