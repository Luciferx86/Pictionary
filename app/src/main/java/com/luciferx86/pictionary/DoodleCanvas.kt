package com.luciferx86.pictionary

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import io.socket.client.IO
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject


class DoodleCanvas(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    private var mPaint: Paint = Paint()
    private var lastPaintStroke: Paint = Paint()
    private var mPath: SerializablePath
    private var pathList: ArrayList<PathPojo> = ArrayList()
    private var backupPathList: ArrayList<PathPojo> = ArrayList()
    private var db = FirebaseFirestore.getInstance();
    private val mSocket = IO.socket("https://pictionary-server.herokuapp.com");
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (path in pathList) {
            Log.d("PathVal", path.toString())
            val pathToDraw = path.path;
            mPaint.color = path.color
            mPaint.strokeWidth = path.strokeWidth
            canvas.drawPath(pathToDraw, mPaint)
        }
//        canvas.drawPath(mPath, mPaint);
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mPath = SerializablePath();

                mPath.moveTo(event.x, event.y)
                pathList.add(
                    PathPojo(
                        mPath,
                        mPaint.color,
                        mPaint.strokeWidth
                    )
                )
                mSocket.emit("touch", event.x / this.width, event.y / this.height);
//                makeFirestoreChanges(DataToDraw("touch", event.x, event.y, mPaint.color, mPaint.strokeWidth));
                invalidate()
                return true;
            }
            MotionEvent.ACTION_MOVE -> {

                mPath.lineTo(event.x, event.y)
//                makeFirestoreChanges(DataToDraw("move", event.x, event.y, mPaint.color, mPaint.strokeWidth));
                //emitting this way so that when receiving, point can be plotted according to screen height size
                mSocket.emit("move", event.x / width, event.y / height);
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return false
    }

    init {
        mPaint.color = Color.RED
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeWidth = 10f
        mPath = SerializablePath()

        setupSocketListeners();

        mSocket.connect();

//        listenToFirebaseChanges();
    }

    fun setupSocketListeners() {
        val onTouchEvent: Emitter.Listener = Emitter.Listener { args ->
            (context as Activity).runOnUiThread { //Code for the UiThread
                val data = args[0] as JSONObject
                var x: String? = null
                var y: String? = null
                try {
                    x = data.getString("touchX")
                    y = data.getString("touchY")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                Log.v("Touch", "$x, $y")
                val touchX = x!!.toFloat()
                val touchY = y!!.toFloat()
                mPath = SerializablePath();
                //moving according to screen size
                mPath.moveTo(touchX * this.width, touchY * this.height)
                pathList.add(
                    PathPojo(
                        mPath,
                        mPaint.color,
                        mPaint.strokeWidth
                    )
                )
                invalidate()
            }
        }
        val onMoveEvent: Emitter.Listener = Emitter.Listener { args ->
            (getContext() as Activity).runOnUiThread {
                val data = args[0] as JSONObject
                var x: String? = null
                var y: String? = null
                try {
                    x = data.getString("touchX")
                    y = data.getString("touchY")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                Log.v("MoveEvent", "$x, $y")
                val touchX = x!!.toFloat()
                val touchY = y!!.toFloat()
                //drawingf according to screen size
                mPath.lineTo(touchX * this.width, touchY * this.height)
                invalidate()
            }
        }

        val onPaintChangeEvent: Emitter.Listener = Emitter.Listener { args ->
            (context as Activity).runOnUiThread {
                val data = args[0] as JSONObject
                var color: String? = null;
                try {
                    color = data.getString("color");
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                Log.v("ColorChangeEvent", "$color");
                val newColor = color!!.toInt();
                this.mPaint.color = newColor;
//                invalidate()
            }
        }

        val onWidthChangeListener: Emitter.Listener = Emitter.Listener { args ->
            (context as Activity).runOnUiThread {
                val data = args[0] as JSONObject
                var width: String? = null;
                try {
                    width = data.getString("width");
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                Log.v("WidthChangeEvent", "$width");
                val newWidth = width!!.toFloat();
                this.mPaint.strokeWidth = newWidth;
//                invalidate()
            }
        }

        val onUndoChangeListener: Emitter.Listener = Emitter.Listener { args ->
            (context as Activity).runOnUiThread {
                val data = args[0] as JSONObject
                var restore: String? = null;
                try {
                    restore = data.getString("restore");
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                Log.v("UndoChangeEvent", "$restore");
                val isRestore = restore!!.toBoolean();
                if (isRestore) {
                    pathList.clear();
                    pathList.addAll(backupPathList);
                } else {
                    if (pathList.size > 0) {
                        pathList.removeAt(pathList.size - 1);
                    }
                }
                invalidate()
            }
        }


        val onClearEventListener: Emitter.Listener = Emitter.Listener { args ->
            (context as Activity).runOnUiThread {
                Log.v("ClearEvent", "clear");
                backupPathList.clear()
                backupPathList.addAll(pathList)
                pathList.clear();
                invalidate()
            }
        }
        val onConnectEvent = Emitter.Listener { (getContext() as Activity).runOnUiThread { } }

        mSocket.on("touch", onTouchEvent);
        mSocket.on("move", onMoveEvent);
        mSocket.on("connected", onConnectEvent);
        mSocket.on("changePaint", onPaintChangeEvent)
        mSocket.on("changeWidth", onWidthChangeListener)
        mSocket.on("undo", onUndoChangeListener);
        mSocket.on("clear", onClearEventListener);
    }


    fun setStrokeColor(color: Int) {
        mPaint.color = color;
        mSocket.emit("changePaint", color);
    }

    fun setStrokeWidth(strokeWidth: Float) {
        mPaint.strokeWidth = strokeWidth;
        mSocket.emit("changeWidth", strokeWidth);
    }

    fun undoMove() {
        if (pathList.size > 0) {
            pathList.removeAt(pathList.size - 1);
            mSocket.emit("undo", false);
        } else {
            pathList.addAll(backupPathList);
            mSocket.emit("undo", true);
        }
//        makeFirestoreChanges(pathList);
        invalidate();
    }

    fun enableErasing() {
        storeLastPaintStroke(mPaint);
        mSocket.emit("changePaint",Color.WHITE);
        mPaint.color = Color.WHITE;
    }

    private fun storeLastPaintStroke(lastPaint: Paint) {
        lastPaintStroke = lastPaint;
    }

    fun enablePainting() {
        mPaint = lastPaintStroke;

    }

    fun clearCanvas() {
        backupPathList.clear()
        backupPathList.addAll(pathList)
        pathList.clear();
        mSocket.emit("clear");
        makeFirestoreChanges(DataToDraw("clear", 0f, 0f, 0, 0f));
        invalidate();
    }

    fun makeFirestoreChanges(newEvent: DataToDraw) {
        val game: HashMap<String, Any> = HashMap()
        game["paths"] = newEvent;
        db.collection("Pictionary")
            .document("games")
            .collection("game2")
            .document("paths").set(game)
    }

    fun updateCanvas(newPathList: ArrayList<PathPojo>) {
        this.pathList.clear();
        this.pathList.addAll(newPathList);
        invalidate();
    }

    class DataToDraw {
        constructor() {
            this.mode = "";
            this.eventX = 0f;
            this.eventY = 0f;
            this.paintColor = Color.RED;
            this.paintWidth = 10f
        }

        val mode: String;
        val eventX: Float;
        val eventY: Float;
        val paintColor: Int;
        val paintWidth: Float

        constructor(
            mode: String,
            eventX: Float,
            eventY: Float,
            paintColor: Int,
            paintWidth: Float
        ) {
            this.mode = mode;
            this.eventY = eventY;
            this.eventX = eventX;
            this.paintColor = paintColor;
            this.paintWidth = paintWidth;
        }
    }
}