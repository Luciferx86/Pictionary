package com.luciferx86.pictionary.View.Activiy

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.luciferx86.pictionary.R
import java.util.*

class CharecterGenerationActivity : AppCompatActivity() {
    lateinit var leftLeg: View;
    lateinit var rightLeg: View;
    lateinit var face: View;
    lateinit var body: View;
    lateinit var leftArm: View;
    lateinit var rightArm: View;
    lateinit var randomizeAvatar: Button;
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charecter_generation)

        initBodyParts();



        randomizeAvatar.setOnClickListener {
            setRandomColor();
        }
    }

    private fun initBodyParts() {
        leftArm = findViewById(R.id.leftArm);
        leftLeg = findViewById(R.id.leftLeg);
        rightArm = findViewById(R.id.rightArm);
        rightLeg = findViewById(R.id.rightLeg);
        face = findViewById(R.id.face);
        body = findViewById(R.id.body);
        randomizeAvatar = findViewById(R.id.randomizeAvatar);
    }

    private fun getRandomColor(): String{
        val obj = Random()
        val rand_num: Int = obj.nextInt(0xffffff + 1)
        val colorCode = String.format("#%06x", rand_num)
        return colorCode;
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setRandomColor(){
        val color = getRandomColor();
        leftArm.backgroundTintList = ColorStateList.valueOf(manipulateColor(Color.parseColor(color), 0.7f));
        leftLeg.backgroundTintList = ColorStateList.valueOf(manipulateColor(Color.parseColor(color), 0.7f));
        rightArm.backgroundTintList = ColorStateList.valueOf(manipulateColor(Color.parseColor(color), 0.7f));
        rightLeg.backgroundTintList = ColorStateList.valueOf(manipulateColor(Color.parseColor(color), 0.7f));
        face.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color));
        body.backgroundTintList = ColorStateList.valueOf(manipulateColor(Color.parseColor(color), 0.7f));
    }

    fun manipulateColor(color: Int, factor: Float): Int {
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
}