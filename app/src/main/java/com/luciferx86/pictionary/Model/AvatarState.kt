package com.luciferx86.pictionary.Model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Luciferx86 on 27/08/20.
 **/
open class AvatarState : Serializable, Parcelable {
    @Expose
    @SerializedName("faceColor")
    var faceColor: Int;

    @Expose
    @SerializedName("hatIndex")
    var hatIndex: Int;

    @Expose
    @SerializedName("eyesIndex")
    var eyesIndex: Int;

    @Expose
    @SerializedName("mouthIndex")
    var mouthIndex: Int;


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(this.faceColor)
        dest.writeInt(this.hatIndex)
        dest.writeInt(this.eyesIndex)
        dest.writeInt(this.mouthIndex)
    }

    override fun toString(): String {
        return "AvatarState(faceColor='$faceColor', hatIndex=$hatIndex, eyesIndex=$eyesIndex, mouthIndex=$mouthIndex)"
    }


    public constructor(incoming: Parcel) {
        faceColor = incoming.readInt();
        hatIndex = incoming.readInt();
        eyesIndex = incoming.readInt();
        mouthIndex = incoming.readInt();
    }


    constructor() {
        faceColor = -1;
        hatIndex = -1;
        eyesIndex = -1;
        mouthIndex = -1;
    }

    constructor(faceColor: Int, hatIndex: Int, eyesIndex: Int, mouthIndex: Int) {
        this.faceColor = faceColor
        this.hatIndex = hatIndex
        this.eyesIndex = eyesIndex
        this.mouthIndex = mouthIndex
    }


    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<AvatarState> = object : Parcelable.Creator<AvatarState> {
            override fun createFromParcel(source: Parcel): AvatarState? {
                return AvatarState(source)
            }

            override fun newArray(size: Int): Array<AvatarState?> {
                return arrayOfNulls(size)
            }
        }
    }
}