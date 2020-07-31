package com.luciferx86.pictionary

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.view.*


class DoodleCanvas(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    private var mPaint: Paint = Paint()
    private var lastPaintStroke: Paint = Paint()
    private var mPath: Path
    private var pathList : ArrayList<Triple<Path, Int, Float>> =  ArrayList()
    private var backupPathList : ArrayList<Triple<Path, Int, Float>> =  ArrayList()
    override fun onDraw(canvas: Canvas) {
        for ((first, second, third) in pathList) {
            val pathToDraw = first;
            mPaint.color = second
            mPaint.strokeWidth = third
            canvas.drawPath(pathToDraw, mPaint)
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

    fun undoMove(){
        if(pathList.size > 0){
            pathList.removeAt(pathList.size - 1);
        }else{
            pathList.addAll(backupPathList);

        }
        invalidate();
    }

    fun enableErasing(){
        storeLastPaintStroke(mPaint);
        mPaint.color = Color.WHITE;
    }

    private fun storeLastPaintStroke(lastPaint : Paint){
        lastPaintStroke = lastPaint;
    }
    fun enablePainting(){
        mPaint = lastPaintStroke;

    }

    fun clearCanvas(){
        backupPathList.clear()
        backupPathList.addAll(pathList)
        pathList.clear();
        invalidate();
    }
}