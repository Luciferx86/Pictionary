package com.luciferx86.pictionary

import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point


class Stroke(val paint: Paint) {
    var path: Path? = null
        private set

    fun addPoint(pt: Point) {
        if (path == null) {
            path = Path()
            path!!.moveTo(pt.x.toFloat(), pt.y.toFloat())
        } else {
            path!!.lineTo(pt.x.toFloat(), pt.y.toFloat())
        }
    }

}