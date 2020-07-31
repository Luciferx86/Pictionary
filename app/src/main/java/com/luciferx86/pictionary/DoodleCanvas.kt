package com.luciferx86.pictionary

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class DoodleCanvas(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    private val mPaint: Paint = Paint()
    private var mPath: Path
    private var pathList : ArrayList<Triple<Path, Int, Float>> =  ArrayList()
    override fun onDraw(canvas: Canvas) {
        for ((first, second, third) in pathList) {
            mPaint.color = second
            mPaint.strokeWidth = third
            canvas.drawPath(first, mPaint)
        }
//        canvas.drawPath(mPath, mPaint)
        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mPath = Path();
                mPath.moveTo(event.x, event.y)
                pathList.add(Triple(mPath, mPaint.color, mPaint.strokeWidth))

//                mPath.moveTo(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {

                mPath.lineTo(event.x, event.y)
//                pathList.add(Triple(mPath, mPaint.color, mPaint.strokeWidth));
//                mPath = Path();
//                mPath.lineTo(event.x, event.y)

                invalidate()
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return true
    }

    init {
        mPaint.color = Color.RED
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeWidth = 10f
        mPath = Path()
    }

    fun setStrokeColor(color: Int) {
        mPaint.color = color;
    }
    fun setStrokeWidth(strokeWidth: Float) {
        mPaint.strokeWidth = strokeWidth;
    }
}