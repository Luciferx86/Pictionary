package com.luciferx86.pictionary

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.Slider
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var colorSelectionRecycler: RecyclerView;
    private lateinit var canvas: DoodleCanvas;
    lateinit var strokeSlider: Slider;
    lateinit var deleteButton: ImageButton;
    lateinit var undoButton: ImageButton;
    lateinit var eraserSelector: ImageButton;
    lateinit var paintSelector: ImageButton;
    lateinit var db : FirebaseFirestore;
    val allColors: ArrayList<Int> = ArrayList();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews();
        setClickListeners();


        addColors();

        val colorAdapter =
            ColorSelectionAdapter(allColors, adapterOnClick = { color -> doColorChange(color) });
        colorSelectionRecycler.adapter = colorAdapter;
        colorSelectionRecycler.layoutManager = GridLayoutManager(this, 6);

        colorSelectionRecycler.addItemDecoration(ColorDecor(8));


//        makeFirestoreChanges();

    }

    fun doColorChange(color: Int) {
        Log.d("ClickEvent", color.toString());
        canvas.setStrokeColor(color);
    }

    fun addColors() {
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
    }

    fun initViews() {
        colorSelectionRecycler = findViewById(R.id.colorOptionsLayout);
        canvas = findViewById(R.id.canvas);
        strokeSlider = findViewById(R.id.slider);
        deleteButton = findViewById(R.id.deleteButton);
        undoButton = findViewById(R.id.undoButton);
        eraserSelector = findViewById(R.id.eraserSelector);
        paintSelector = findViewById(R.id.paintSelector);
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
    }

    fun setClickListeners() {

        slider.addOnChangeListener { slider, value, fromUser ->
            // Responds to when slider's value is changed
            Log.d("sliderVal", value.toString());
            canvas.setStrokeWidth(value);
        }

        undoButton.setOnClickListener {
            canvas.undoMove();
        }

        eraserSelector.setOnClickListener {
            canvas.enableErasing();
        }
        paintSelector.setOnClickListener {
            canvas.enablePainting();
        }

        deleteButton.setOnClickListener {
            canvas.clearCanvas();
        }
    }

    fun makeFirestoreChanges(){
        val game: HashMap<String, Any> = HashMap()
        game["game1"] = "game1";
        db.collection("Pictionary").document().collection("games").add(game);
    }
}