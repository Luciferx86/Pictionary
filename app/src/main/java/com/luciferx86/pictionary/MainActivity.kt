package com.luciferx86.pictionary

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.Slider
import com.google.common.primitives.UnsignedBytes.toInt
import com.google.firebase.FirebaseApp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    lateinit var colorSelectionRecycler: RecyclerView;
    lateinit var activePlayersRecycler: RecyclerView;
    private lateinit var canvas: DoodleCanvas;
    lateinit var strokeSlider: Slider;
    lateinit var deleteButton: ImageButton;
    lateinit var undoButton: ImageButton;
    lateinit var eraserSelector: ImageButton;
    lateinit var paintSelector: ImageButton;
    lateinit var startGameButton: Button;
    lateinit var gameCodeText: TextView;
    lateinit var gameInfoLayout: ConstraintLayout;
    val allColors: ArrayList<Int> = ArrayList();
    val allPlayers: ArrayList<SinglePlayerPojo> = ArrayList();
    val activePlayersAdapter = ActivePlayersAdapter(allPlayers);
    val colorAdapter =
        ColorSelectionAdapter(allColors, adapterOnClick = { color -> doColorChange(color) });

    private val mSocket = IO.socket("https://pictionary-server.herokuapp.com");
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews();
        setClickListeners();

        initGameState();

        initRecyclers();

        setSocketListeners();
        mSocket.connect();

        addColors();



//        allPlayers.add("Luciferx86");
//        allPlayers.add("smolbean");


    }

    fun initGameState() {

        val gameState: String? = intent.getStringExtra("gameState");
        val gameStateObject: GameStatePojo =
            Gson().fromJson(gameState, object : TypeToken<GameStatePojo>() {}.type)
        Log.d("GameState", gameStateObject.toString());
        gameStateObject.players.forEach {
            allPlayers.add(it);
        }
        activePlayersAdapter.notifyDataSetChanged();
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
        activePlayersRecycler = findViewById(R.id.activePlayersRecycler);
        canvas = findViewById(R.id.canvas);
        strokeSlider = findViewById(R.id.slider);
        deleteButton = findViewById(R.id.deleteButton);
        undoButton = findViewById(R.id.undoButton);
        eraserSelector = findViewById(R.id.eraserSelector);
        paintSelector = findViewById(R.id.paintSelector);
        gameCodeText = findViewById(R.id.gameCodeNew);
        startGameButton = findViewById(R.id.startGameButton);
        gameInfoLayout = findViewById(R.id.gameInfoLayout);
        FirebaseApp.initializeApp(this);
        val code: Int = intent.getIntExtra("gameCode", 0);
        gameCodeText.text = code.toString()
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

        startGameButton.setOnClickListener {
            gameInfoLayout.visibility = View.GONE;
        }
    }

    fun initRecyclers() {

        colorSelectionRecycler.adapter = colorAdapter;
        colorSelectionRecycler.layoutManager = GridLayoutManager(this, 6);

        colorSelectionRecycler.addItemDecoration(ColorDecor(8));


        activePlayersRecycler.adapter = activePlayersAdapter;
        activePlayersRecycler.layoutManager = LinearLayoutManager(applicationContext);
    }

    fun setSocketListeners(){
        mSocket.on("joinGame", Emitter.Listener { args ->
            runOnUiThread { //Code for the UiThread
                val data = args[0] as JSONObject
                val newPlayer = data["newPlayer"] as JSONObject;
                Log.d("NewPLayer", newPlayer.toString());
                var playerName: String? = null
                var playerScore: String? = null
                var playerRank:String? = null
                try {
                    playerName = newPlayer.getString("playerName")
                    playerScore = newPlayer.getString("score")
                    playerRank = newPlayer.getString("rank")
                    allPlayers.add(SinglePlayerPojo(playerName, playerScore.toInt(), playerRank.toInt()));
                    activePlayersAdapter.notifyDataSetChanged();
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }
}