package com.luciferx86.pictionary.Model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GameStatePojo : Serializable, Parcelable {
    @Expose
    @SerializedName("players")
    var players: ArrayList<SinglePlayerPojo>;


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeList(this.players)
    }

    override fun toString(): String {
        return "GameStatePojo(players=$players)"
    }


    protected constructor(incoming: Parcel) {
        players = ArrayList()
        incoming.readList(players, SinglePlayerPojo::class.java.classLoader)
    }

    constructor(players: ArrayList<SinglePlayerPojo>) {
        this.players = players
    }

    constructor(){
        this.players = ArrayList();
    }



    companion object {
        val CREATOR: Parcelable.Creator<GameStatePojo> = object : Parcelable.Creator<GameStatePojo> {
            override fun createFromParcel(source: Parcel): GameStatePojo? {
                return GameStatePojo(source)
            }

            override fun newArray(size: Int): Array<GameStatePojo?> {
                return arrayOfNulls(size)
            }
        }
    }
}