package com.luciferx86.pictionary.View.Activiy

import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.CalendarContract
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.luciferx86.pictionary.R
import kotlinx.android.synthetic.main.activity_charecter_generation.*
import java.io.File
import java.io.FileOutputStream
import java.util.*


class CharecterGenerationActivity : AppCompatActivity() {
    lateinit var leftLeg: View;
    lateinit var rightLeg: View;
    lateinit var face: View;
    lateinit var body: View;
    lateinit var leftArm: View;
    lateinit var rightArm: View;
    lateinit var randomizeAvatar: Button;
    lateinit var avatarLayout: ConstraintLayout;

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charecter_generation)

        initBodyParts();



        randomizeAvatar.setOnClickListener {
            setRandomColor();
        }
    }

    private fun randomizeEyes() {
        val imgs: TypedArray = resources.obtainTypedArray(R.array.allEyes);
        val random = Random();
        val index = random.nextInt(imgs.length());
        eyes.background = imgs.getDrawable(index);
    }

    private fun initBodyParts() {
        leftArm = findViewById(R.id.leftArm);
        leftLeg = findViewById(R.id.leftLeg);
        rightArm = findViewById(R.id.rightArm);
        rightLeg = findViewById(R.id.rightLeg);
        face = findViewById(R.id.face);
        body = findViewById(R.id.body);
        randomizeAvatar = findViewById(R.id.randomizeAvatar);

        avatarLayout = findViewById(R.id.avatarLayout);


    }

    private fun getRandomColor(): Int {
        val obj = Random()
        return Color.rgb(obj.nextInt(154) + 100, obj.nextInt(154) + 100, obj.nextInt(154) + 100);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setRandomColor() {
        val color = getRandomColor();
        leftArm.backgroundTintList =
            ColorStateList.valueOf(manipulateColor(color, 0.7f));
        leftLeg.backgroundTintList =
            ColorStateList.valueOf(manipulateColor(color, 0.7f));
        rightArm.backgroundTintList =
            ColorStateList.valueOf(manipulateColor(color, 0.7f));
        rightLeg.backgroundTintList =
            ColorStateList.valueOf(manipulateColor(color, 0.7f));
        face.backgroundTintList = ColorStateList.valueOf(color);
        body.backgroundTintList =
            ColorStateList.valueOf(manipulateColor(color, 0.7f));

        randomizeEyes();

        saveImage(getBitmapFromView(avatarLayout));
    }

    private fun manipulateColor(color: Int, factor: Float): Int {
        val a: Int = Color.alpha(color)
        val r = Math.round(Color.red(color) * factor).toInt()
        val g = Math.round(Color.green(color) * factor).toInt()
        val b = Math.round(Color.blue(color) * factor).toInt()
        return Color.argb(
            a,
            Math.min(r, 255),
            Math.min(g, 255),
            Math.min(b, 255)
        )
    }

    private fun getBitmapFromView(view: View): Bitmap? {
        //Define a bitmap with the same size as the view
        view.measure(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        val returnedBitmap =
            Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        //Bind a canvas to it
        val canvas = Canvas(returnedBitmap)
        //Get the view's background
        val bgDrawable: Drawable? = ColorDrawable(Color.TRANSPARENT);
        if (bgDrawable != null) //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas) else  //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE)
        // draw the view on the canvas
        view.draw(canvas)
        //return the bitmap
        return returnedBitmap
    }

    private fun saveImage(finalBitmap: Bitmap?) {
        val root: String = Environment.getExternalStorageDirectory().getAbsolutePath()
        val myDir = File("$root/saved_images")
        myDir.mkdirs()
        val fname = "Image-" + ".jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap?.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}