package com.luciferx86.pictionary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.Slider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var colorSelectionRecycler: RecyclerView;
    private lateinit var canvas: DoodleCanvas;
    lateinit var strokeSlider: Slider;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        colorSelectionRecycler = findViewById(R.id.colorOptionsLayout);
        canvas = findViewById(R.id.canvas);
        strokeSlider = findViewById(R.id.slider);
        val allColors: ArrayList<Int> = ArrayList();
        allColors.add(Color.RED);
        allColors.add(Color.BLUE);
        allColors.add(Color.GRAY);
        allColors.add(Color.GREEN);
        allColors.add(Color.GREEN);
        allColors.add(Color.GREEN);
        allColors.add(Color.GREEN);
        allColors.add(Color.GREEN);
        allColors.add(Color.GREEN);
        allColors.add(Color.GREEN);
        allColors.add(Color.GREEN);
        allColors.add(Color.LTGRAY);
        allColors.add(Color.MAGENTA);
        allColors.add(Color.CYAN);
        allColors.add(Color.BLACK);
        allColors.add(Color.YELLOW);

        var colorAdapter: ColorSelectionAdapter =
            ColorSelectionAdapter(allColors, adapterOnClick = { color -> doClick(color) });
        colorSelectionRecycler.adapter = colorAdapter;
        colorSelectionRecycler.layoutManager = GridLayoutManager(this, 6);

        colorSelectionRecycler.addItemDecoration(ColorDecor(8));

        slider.addOnChangeListener { slider, value, fromUser ->
            // Responds to when slider's value is changed
            Log.d("sliderVal", value.toString());
            canvas.setStrokeWidth(value);
        }
    }

    fun doClick(color: Int) {
        Log.d("ClickEvent", color.toString());
        canvas.setStrokeColor(color);
    }
}