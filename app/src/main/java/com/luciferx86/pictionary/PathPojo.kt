package com.luciferx86.pictionary

import android.graphics.Path
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class PathPojo : Serializable, Parcelable {
    @Expose
    @SerializedName("docType")
    var path: SerializablePath;

    @Expose
    @SerializedName("docName")
    var color: Int;

    @Expose
    @SerializedName("docTitle")
    var strokeWidth: Float;




    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeSerializable(this.path)
        dest.writeInt(this.color)
        dest.writeFloat(this.strokeWidth)
    }

    override fun toString(): String {
        return "PathPojo(path=$path, color=$color, strokeWidth=$strokeWidth"
    }


    protected constructor(incoming: Parcel) {
        path = incoming.readSerializable() as SerializablePath;
        color = incoming.readInt();
        strokeWidth = incoming.readFloat();
    }

    constructor(path: SerializablePath, color: Int, strokeWidth: Float) {
        this.path = path
        this.color = color
        this.strokeWidth = strokeWidth
    }

    constructor(){
        this.path = SerializablePath();
        this.color = 0;
        this.strokeWidth = 10f;
    }



    companion object {
        val CREATOR: Parcelable.Creator<PathPojo> = object : Parcelable.Creator<PathPojo> {
            override fun createFromParcel(source: Parcel): PathPojo? {
                return PathPojo(source)
            }

            override fun newArray(size: Int): Array<PathPojo?> {
                return arrayOfNulls(size)
            }
        }
    }
}
