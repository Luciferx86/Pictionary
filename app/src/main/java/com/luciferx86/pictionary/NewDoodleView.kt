package com.luciferx86.pictionary

import android.content.Context
import android.graphics.*
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import java.util.*


class NewDoodleView(context: Context?) : View(context) {
    private val _allStrokes //all strokes that need to be drawn
            : MutableList<Stroke>?
    private val _activeStrokes //use to retrieve the currently drawn strokes
            : SparseArray<Stroke>
    private val _rdmColor = Random()
    public override fun onDraw(canvas: Canvas) {
        if (_allStrokes != null) {
            for (stroke in _allStrokes) {
                val path: Path? = stroke.path
                val painter: Paint = stroke.paint
                if (path != null) {
                    canvas.drawPath(path, painter)
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        val pointerCount = event.pointerCount
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                pointDown(event.x.toInt(), event.y.toInt(), event.getPointerId(0))
            }
            MotionEvent.ACTION_MOVE -> {
                var pc = 0
                while (pc < pointerCount) {
                    pointMove(
                        event.getX(pc).toInt(),
                        event.getY(pc).toInt(),
                        event.getPointerId(pc)
                    )
                    pc++
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                var pc = 0
                while (pc < pointerCount) {
                    pointDown(
                        event.getX(pc).toInt(),
                        event.getY(pc).toInt(),
                        event.getPointerId(pc)
                    )
                    pc++
                }
            }
            MotionEvent.ACTION_UP -> {
            }
            MotionEvent.ACTION_POINTER_UP -> {
            }
        }
        invalidate()
        return true
    }

    private fun pointDown(x: Int, y: Int, id: Int) {
        //create a paint with random color
        val paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        paint.color = _rdmColor.nextInt()

        //create the Stroke
        val pt = Point(x, y)
        val stroke = Stroke(paint)
        stroke.addPoint(pt)
        _activeStrokes.put(id, stroke)
        _allStrokes!!.add(stroke)
    }

    private fun pointMove(x: Int, y: Int, id: Int) {
        //retrieve the stroke and add new point to its path
        val stroke: Stroke? = _activeStrokes[id]
        if (stroke != null) {
            val pt = Point(x, y)
            stroke.addPoint(pt)
        }
    }

    init {
        _allStrokes = ArrayList<Stroke>()
        _activeStrokes = SparseArray<Stroke>()
        isFocusable = true
        isFocusableInTouchMode = true
        setBackgroundColor(Color.WHITE)
    }
}