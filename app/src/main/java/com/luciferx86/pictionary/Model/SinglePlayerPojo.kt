package com.luciferx86.pictionary.Model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class SinglePlayerPojo : Serializable, Parcelable {
    @Expose
    @SerializedName("playerName")
    var playerName: String;

    @Expose
    @SerializedName("score")
    var score: Int;

    @Expose
    @SerializedName("rank")
    var rank: Int;

    @Expose
    @SerializedName("playerAvatar")
    var playerAvatar: AvatarState;


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.playerName)
        dest.writeInt(this.score)
        dest.writeInt(this.rank)
        dest.writeSerializable(this.playerAvatar);
    }

    public constructor(incoming: Parcel) {
        playerName = incoming.readString()!!;
        score = incoming.readInt();
        rank = incoming.readInt();
        playerAvatar = incoming.readSerializable() as AvatarState;
    }

    override fun toString(): String {
        return "SinglePlayerPojo(playerName='$playerName', score=$score, rank=$rank)"
    }


    constructor(playerName: String, score: Int, rank: Int, playerAvatar: AvatarState) {
        this.playerName = playerName
        this.score = score
        this.rank = rank
        this.playerAvatar = playerAvatar;
    }

    constructor() {
        this.playerName = "dummyPlayer"
        this.score = 0;
        this.rank = 0;
        this.playerAvatar = AvatarState(-1914910, 0, 0, -1);
    }


    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SinglePlayerPojo> =
            object : Parcelable.Creator<SinglePlayerPojo> {
                override fun createFromParcel(source: Parcel): SinglePlayerPojo? {
                    return SinglePlayerPojo(source)
                }

                override fun newArray(size: Int): Array<SinglePlayerPojo?> {
                    return arrayOfNulls(size)
                }
            }
    }
}