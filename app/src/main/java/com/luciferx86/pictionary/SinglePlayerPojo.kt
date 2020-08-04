package com.luciferx86.pictionary

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SinglePlayerPojo : Serializable, Parcelable {
    @Expose
    @SerializedName("playerName")
    var playerName: String;

    @Expose
    @SerializedName("score")
    var score: Int;

    @Expose
    @SerializedName("rank")
    var rank: Int;




    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.playerName)
        dest.writeInt(this.score)
        dest.writeInt(this.rank)
    }

    override fun toString(): String {
        return "SinglePlayerPojo(playerName='$playerName', score=$score, rank=$rank)"
    }


    protected constructor(incoming: Parcel) {
        playerName = incoming.readString()!!;
        score = incoming.readInt();
        rank = incoming.readInt();
    }

    constructor(playerName: String, score: Int, rank: Int) {
        this.playerName = playerName
        this.score = score
        this.rank = rank
    }

    constructor(){
        this.playerName = "dummyPlayer"
        this.score = 0;
        this.rank = 0;
    }



    companion object {
        val CREATOR: Parcelable.Creator<SinglePlayerPojo> = object : Parcelable.Creator<SinglePlayerPojo> {
            override fun createFromParcel(source: Parcel): SinglePlayerPojo? {
                return SinglePlayerPojo(source)
            }

            override fun newArray(size: Int): Array<SinglePlayerPojo?> {
                return arrayOfNulls(size)
            }
        }
    }
}