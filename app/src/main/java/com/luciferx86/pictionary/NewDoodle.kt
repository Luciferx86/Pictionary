package com.luciferx86.pictionary

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson


class NewDoodle(c: Context, attrs: AttributeSet?) : View(c, attrs) {
    private var mPaint: Paint? = null
    lateinit var mBitmap: Bitmap;
    private var mCanvas: Canvas? = null
    var mPath: Path = Path()
    private var mBitmapPaint: Paint? = null
    var circlePaint: Paint
    var circlePath: Path
    private var db = FirebaseFirestore.getInstance();
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mBitmap, 0f, 0f, mBitmapPaint)

        mPaint?.let { canvas.drawPath(mPath, it) }
        canvas.drawPath(circlePath, circlePaint)
//        makeFirestoreChanges(bitmap);
    }

    private fun touch_start(x: Float, y: Float) {
        mPath.reset()
        mPath.moveTo(x, y)
        mX = x
        mY = y
    }

    private var mX = 0f
    private var mY = 0f


    private fun touch_move(x: Float, y: Float) {
        val dx = Math.abs(x - mX)
        val dy = Math.abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
            circlePath.reset()
            circlePath.addCircle(mX, mY, 30f, Path.Direction.CW)
        }
    }

    private fun touch_up() {
        mPath.lineTo(mX, mY)
        circlePath.reset()
        // commit the path to our offscreen
        mPaint?.let {
            mCanvas?.drawPath(mPath, it)
        }
        // kill this so we don't double draw
        mPath.reset()
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touch_start(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touch_move(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touch_up()
                invalidate()
            }
        }
        return true
    }

    companion object {
        private const val TOUCH_TOLERANCE = 4f
    }

    init {
        mBitmapPaint = Paint(Paint.DITHER_FLAG)
        circlePaint = Paint()
        circlePath = Path()
        circlePaint.isAntiAlias = true
        circlePaint.color = Color.BLUE
        circlePaint.style = Paint.Style.STROKE
        circlePaint.strokeJoin = Paint.Join.MITER
        circlePaint.strokeWidth = 4f
        mPaint = Paint()
        mPaint?.isAntiAlias = true
        mPaint?.isDither = true
        mPaint?.color = Color.GREEN
        mPaint?.style = Paint.Style.STROKE
        mPaint?.strokeJoin = Paint.Join.ROUND
        mPaint?.strokeCap = Paint.Cap.ROUND
        mPaint?.strokeWidth = 12f


    }

    fun makeFirestoreChanges(pathList: Bitmap) {
        val game: HashMap<String, Any> = HashMap()
        val newVal:String = Gson().toJson(pathList)
        game["paths"] =newVal
        db.collection("Pictionary")
            .document("games")
            .collection("game2")
            .document("paths").set(game)
    }
}