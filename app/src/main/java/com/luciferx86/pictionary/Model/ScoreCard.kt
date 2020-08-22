package com.luciferx86.pictionary.Model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Luciferx86 on 22/08/20.
 */
open class ScoreCard : Serializable, Parcelable {
    @Expose
    @SerializedName("playerName")
    var playerName: String;

    @Expose
    @SerializedName("score")
    var score: Int;


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.playerName)
        dest.writeInt(this.score)
    }

    override fun toString(): String {
        return "ScoreCard(playerName='$playerName', score=$score)"
    }

    public constructor(incoming: Parcel) {
        playerName = incoming.readString()!!;
        score = incoming.readInt();
    }





    constructor(playerName: String, score: Int, rank: Int) {
        this.playerName = playerName
        this.score = score
    }

    constructor(){
        this.playerName = "dummyPlayer"
        this.score = 0;
    }



    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ScoreCard> = object : Parcelable.Creator<ScoreCard> {
            override fun createFromParcel(source: Parcel): ScoreCard? {
                return ScoreCard(source)
            }

            override fun newArray(size: Int): Array<ScoreCard?> {
                return arrayOfNulls(size)
            }
        }
    }
}