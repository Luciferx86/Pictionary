package com.luciferx86.pictionary.Model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Luciferx86 on 22/08/20.
 */
class ScoreSheet : Serializable, Parcelable {
    @Expose
    @SerializedName("allScores")
    var allScores: ArrayList<ScoreCard>;


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeList(this.allScores)
    }

    override fun toString(): String {
        return "ScoreSheet(allScores=$allScores)"
    }


    protected constructor(incoming: Parcel) {
        allScores = ArrayList()
        incoming.readList(allScores, SinglePlayerPojo::class.java.classLoader)
    }

    constructor(allScores: ArrayList<ScoreCard>) {
        this.allScores = allScores
    }

    constructor(){
        this.allScores = ArrayList();
    }



    companion object {
        val CREATOR: Parcelable.Creator<ScoreSheet> = object : Parcelable.Creator<ScoreSheet> {
            override fun createFromParcel(source: Parcel): ScoreSheet? {
                return ScoreSheet(source)
            }

            override fun newArray(size: Int): Array<ScoreSheet?> {
                return arrayOfNulls(size)
            }
        }
    }
}