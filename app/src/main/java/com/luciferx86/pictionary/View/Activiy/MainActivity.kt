package com.luciferx86.pictionary.View.Activiy

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.Slider
import com.google.firebase.FirebaseApp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.luciferx86.pictionary.Model.*
import com.luciferx86.pictionary.R
import com.luciferx86.pictionary.RootApplication.mSocket
import com.luciferx86.pictionary.Utils.ColorDecor
import com.luciferx86.pictionary.Utils.DoodleCanvas
import com.luciferx86.pictionary.View.Adapter.ActivePlayersAdapter
import com.luciferx86.pictionary.View.Adapter.ChatRecyclerAdapter
import com.luciferx86.pictionary.View.Adapter.ColorSelectionAdapter
import io.socket.client.Ack
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    lateinit var drawingOptionsLayout: LinearLayout;
    var canDraw: Boolean = false;
    private var currentPlayer: SinglePlayerPojo? = null;
    var gameCode: Int = 0;
    lateinit var colorSelectionRecycler: RecyclerView;
    private lateinit var activePlayersRecycler: RecyclerView;
    lateinit var chatRecycler: RecyclerView;
    private lateinit var canvas: DoodleCanvas;
    lateinit var currentWord: TextView;
    lateinit var timerCount: TextView;
    lateinit var waitTimer: CountDownTimer;
    var timerVal: Int = 90;
    lateinit var strokeSlider: Slider;
    lateinit var deleteButton: ImageButton;
    lateinit var undoButton: ImageButton;
    lateinit var eraserSelector: ImageButton;
    lateinit var paintSelector: ImageButton;
    lateinit var paintBucket: ImageButton;
    lateinit var firstWord: TextView;
    lateinit var secondWord: TextView;
    lateinit var thirdWord: TextView;
    lateinit var chatMessageEditText: TextView;
    private lateinit var startGameButton: Button;
    lateinit var wordSelectionLayout: ConstraintLayout;
    lateinit var gameCodeText: TextView;
    lateinit var sendMessageButton: ImageView;
    lateinit var gameInfoLayout: ConstraintLayout;
    lateinit var whoseTurnLayout: ConstraintLayout;
    lateinit var whoseTurnText: TextView;
    private val allColors: ArrayList<Int> = ArrayList();
    private val allPlayers: ArrayList<SinglePlayerPojo> = ArrayList();
    private val activePlayersAdapter =
        ActivePlayersAdapter(allPlayers);
    private val allMessages = ArrayList<ChatPojo>();
    private val colorAdapter =
        ColorSelectionAdapter(
            allColors,
            adapterOnClick = { color -> doColorChange(color) });

    private val chatAdapter = ChatRecyclerAdapter(this, allMessages);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews();
        initTimer();
        setSocketListeners();
        initGameState();
        setClickListeners();
        initRecyclers()
        addColors();
    }

    private fun getPlayerNameFromRank(rank: Int): String {
        for (player: SinglePlayerPojo in allPlayers) {
            if (player.rank == rank) {
                return player.playerName;
            }
        }
        return "";
    }

    private fun initTimer() {
        timerVal = 90;
        waitTimer = object : CountDownTimer(91 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                //called every 300 milliseconds, which could be used to
                //send messages or some other action
                runOnUiThread {
                    timerCount.text = timerVal.toString();
                    timerVal--;
                }

            }

            override fun onFinish() {
                //After 60000 milliseconds (60 sec) finish current
                //if you would like to execute something when time finishes
            }
        }
    }


    private fun initGameState() {
        currentPlayer = intent.getParcelableExtra("newPlayer");
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

    private fun addColors() {
        allColors.add(Color.parseColor("#ffffff"));
        allColors.add(Color.parseColor("#cfcfcf"));
        allColors.add(Color.parseColor("#ff0000"));
        allColors.add(Color.parseColor("#ff9900"));
        allColors.add(Color.parseColor("#E0AC69"));
        allColors.add(Color.parseColor("#ffff00"));
        allColors.add(Color.parseColor("#a2ff00"));
        allColors.add(Color.parseColor("#37ff00"));
        allColors.add(Color.parseColor("#00ff99"));
        allColors.add(Color.parseColor("#00ffe1"));
        allColors.add(Color.parseColor("#00c8ff"));
        allColors.add(Color.parseColor("#001aff"));
        allColors.add(Color.parseColor("#a01ef7"));
        allColors.add(Color.parseColor("#d400ff"));
        allColors.add(Color.parseColor("#f5b0e5"));
        allColors.add(Color.parseColor("#c9661e"));
        allColors.add(Color.parseColor("#8b4513"));
        allColors.add(Color.parseColor("#000000"));
    }

    fun initViews() {
        currentWord = findViewById(R.id.currentWord);
        firstWord = findViewById(R.id.firstWord)
        secondWord = findViewById(R.id.secondWord)
        thirdWord = findViewById(R.id.thirdWord)
        colorSelectionRecycler = findViewById(R.id.colorOptionsLayout);
        activePlayersRecycler = findViewById(R.id.activePlayersRecycler);
        timerCount = findViewById(R.id.timerCount);
        chatRecycler = findViewById(R.id.chatAreaLayout);
        wordSelectionLayout = findViewById(R.id.wordSelectionLayout);
        drawingOptionsLayout = findViewById(R.id.drawingOptionsLayout);
        canvas = findViewById(R.id.canvas);
        strokeSlider = findViewById(R.id.slider);
        deleteButton = findViewById(R.id.deleteButton);
        undoButton = findViewById(R.id.undoButton);
        eraserSelector = findViewById(R.id.eraserSelector);
        paintSelector = findViewById(R.id.paintSelector);
        gameCodeText = findViewById(R.id.gameCodeNew);
        startGameButton = findViewById(R.id.startGameButton);
        gameInfoLayout = findViewById(R.id.gameInfoLayout);
        chatMessageEditText = findViewById(R.id.chatMessageEditText);
        whoseTurnLayout = findViewById(R.id.whoseTurnLayout);
        whoseTurnText = findViewById(R.id.whoseTurnText);
        paintBucket = findViewById(R.id.paintBucket);
        sendMessageButton = findViewById(R.id.sendGuess);

        FirebaseApp.initializeApp(this);
        gameCode = intent.getIntExtra("gameCode", 0);
        gameCodeText.text = gameCode.toString()
        val gameCreator = intent.getBooleanExtra("gameCreator", false);
        if (!gameCreator) {
            startGameButton.visibility = View.GONE;
        }

        canvas.canUserDraw(false);
    }

    private fun setClickListeners() {

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

            mSocket?.emit("startGame", Ack {
                runOnUiThread {
                    wordSelectionLayout.visibility = View.VISIBLE;
                    val data = it[0] as JSONObject;
                    val randWords = data["randomWords"] as JSONArray;
                    firstWord.text = randWords[0].toString();
                    secondWord.text = randWords[1].toString();
                    thirdWord.text = randWords[2].toString();

                    gameInfoLayout.visibility = View.GONE;
                }
            });
        }

        sendMessageButton.setOnClickListener {
//            wordSelectionLayout.visibility = View.VISIBLE
            sendGuess();
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(sendMessageButton.windowToken, 0)
        }

        paintBucket.setOnClickListener {

            generateRandomWords();
        }

        firstWord.setOnClickListener {
            this.callWordSelection(firstWord.text.toString());
        }

        secondWord.setOnClickListener {
            this.callWordSelection(secondWord.text.toString());
        }

        thirdWord.setOnClickListener {
            this.callWordSelection(thirdWord.text.toString());
        }
    }

    private fun callWordSelection(value: String) {
        Log.d("SelectedWord", value);
        wordSelectionLayout.visibility = View.GONE;
        mSocket?.emit("wordSelect", value, currentPlayer?.playerName, Ack {
            runOnUiThread {
                makDrawingViewsVisible();
                val data = it[0] as JSONObject;
                val wordHint = data["wordHint"] as String;
                currentWord.text = wordHint;
                Log.d("WordPosted", value);
                restartTimer();
            }
        })
    }

    private fun restartTimer() {
        timerVal = 90;
        waitTimer.start();
    }

    private fun cancelTimer() {
        waitTimer.cancel();
    }


    private fun initRecyclers() {

        colorSelectionRecycler.adapter = colorAdapter;
        colorSelectionRecycler.layoutManager = GridLayoutManager(this, 6);

        colorSelectionRecycler.addItemDecoration(
            ColorDecor(
                8
            )
        );


        activePlayersRecycler.adapter = activePlayersAdapter;
        activePlayersRecycler.layoutManager = LinearLayoutManager(applicationContext);


        chatRecycler.adapter = chatAdapter;
        chatRecycler.layoutManager = LinearLayoutManager(applicationContext);

    }

    fun parsePlayerFromJSON(json: JSONObject): SinglePlayerPojo {
        var playerName: String? = null
        var playerScore: String? = null
        var playerRank: String? = null
        try {
            playerName = json.getString("playerName")
            playerScore = json.getString("score")
            playerRank = json.getString("rank")
            val newPlayer = SinglePlayerPojo(playerName, playerScore.toInt(), playerRank.toInt());
            return newPlayer;
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return SinglePlayerPojo();
    }

    private fun setSocketListeners() {
        mSocket?.on("joinGame", Emitter.Listener { args ->
            runOnUiThread { //Code for the UiThread
                val data = args[0] as JSONObject
                val newPlayerJson = data["newPlayer"] as JSONObject;
                val newPLayer = parsePlayerFromJSON(newPlayerJson);
                allPlayers.add(newPLayer);
                activePlayersAdapter.notifyDataSetChanged();
            }
        });
        mSocket?.on("newMessage", Emitter.Listener { args ->
            runOnUiThread { //Code for the UiThread
                val data = args[0] as JSONObject
                val newMessage = data["newMessage"] as JSONObject;
                Log.d("NewMessage", newMessage.toString());
                var messageBody: String? = null
                var messageFrom: String? = null
                try {
                    messageBody = newMessage.getString("messageBody")
                    messageFrom = newMessage.getString("messageFrom")
                    allMessages.add(
                        ChatPojo(
                            messageBody,
                            messageFrom
                        )
                    );
                    chatAdapter.notifyDataSetChanged();
                    autoScrollChat();
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        });
        mSocket?.on("turnChange", Emitter.Listener { args ->
            canvas.clearCanvas();
            cancelTimer();
            val data = args[0] as JSONObject
            val whoseTurn = data["whoseTurn"] as Int;
            Log.d("TurnChangeRed", whoseTurn.toString());
            Log.d("TurnChangePlayer", currentPlayer?.rank.toString())
            if (whoseTurn != currentPlayer?.rank) {
                canDraw = false;
                canvas.canUserDraw(false);
                runOnUiThread {
                    drawingOptionsLayout.visibility = View.GONE;
                }
            } else {
                mSocket?.emit("genRandomWords", Ack { words ->
                    val data = words[0] as JSONObject;
                    val randWords = data["randomWords"] as JSONArray;
                    firstWord.text = randWords[0].toString();
                    secondWord.text = randWords[1].toString();
                    thirdWord.text = randWords[2].toString();
                    runOnUiThread {
                        wordSelectionLayout.visibility = View.VISIBLE;
                    }
                })
            }
        });

        mSocket?.on("startGame", Emitter.Listener { args ->
            runOnUiThread { //Code for the UiThread
                canDraw = false;
                canvas.canUserDraw(false);
                drawingOptionsLayout.visibility = View.GONE;
                gameInfoLayout.visibility = View.GONE;
            }
        });

        mSocket?.on("wordSelect", Emitter.Listener { args ->
            runOnUiThread { //Code for the UiThread
                val data = args[0] as JSONObject;
                val wordHint = data["wordHint"] as String;
                val whoseDrawing = data["whoseDrawing"] as String;
                currentWord.text = wordHint
                showWhoseTurn(whoseDrawing);
            }
        });

        mSocket?.on("timerVal", Emitter.Listener { args ->
            runOnUiThread { //Code for the UiThread
                val data = args[0] as JSONObject;
                val timerVal = data["timerVal"] as Int;
                timerCount.text = timerVal.toString();
            }
        });

        mSocket?.on("allScores") { args ->
            val data = args[0] as JSONObject;
            val allScoresString = data["allScores"];
            val allScores: ArrayList<ScoreCard> =
                Gson().fromJson(
                    allScoresString.toString(),
                    object : TypeToken<ArrayList<ScoreCard>>() {}.type
                )
            runOnUiThread {
                updateGameScores(allScores);
            }
        }
    }

    private fun showWhoseTurn(playerName: String) {
        whoseTurnText.text = "$playerName is Drawing!"
        whoseTurnLayout.visibility = View.VISIBLE;
        val animation: Animation = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.drawing_bounce
        )
        startGameButton.startAnimation(animation);
        Looper.myLooper()?.let {
            Handler(it).postDelayed({
                //Your Code
                runOnUiThread {
                    whoseTurnLayout.visibility = View.GONE;
                }
            }, 2000)
        }

    }

    private fun updateGameScores(scores: ArrayList<ScoreCard>) {
        for (score in scores) {
            for (player in allPlayers) {
                if (score.playerName == player.playerName) {
                    player.score = score.score;
                    break;
                }
            }
        }
        activePlayersAdapter.notifyDataSetChanged();
    }

    private fun sendGuess() {
        if (chatMessageEditText.text.toString().isNotEmpty()) {
            val guess = chatMessageEditText.text.toString();
            chatMessageEditText.text = "";
            mSocket?.emit("newMessage", guess, currentPlayer?.rank, timerVal, Ack { it ->
                val data = it[0] as JSONObject;
                Log.d("NewMessage", it[0].toString());
                val correctGuess = data["wordGuessed"] as Boolean;
                val isMyTurn = data["isMyTurn"] as Boolean;
                Log.d("NewMessage", correctGuess.toString());
                if (correctGuess) {
                    allMessages.add(ChatPojo("You Guessed the word!", "Game"));
                    if (isMyTurn) {
                        val allScoresString = data["allScores"];
                        val allScores: ArrayList<ScoreCard> =
                            Gson().fromJson(
                                allScoresString.toString(),
                                object : TypeToken<ArrayList<ScoreCard>>() {}.type
                            )
                        runOnUiThread {
                            updateGameScores(allScores);
                        }
                        mSocket?.emit("genRandomWords", Ack { words ->
                            val data = words[0] as JSONObject;
                            val randWords = data["randomWords"] as JSONArray;
                            firstWord.text = randWords[0].toString();
                            secondWord.text = randWords[1].toString();
                            thirdWord.text = randWords[2].toString();
                            runOnUiThread {
                                wordSelectionLayout.visibility = View.VISIBLE;
                            }
                        })

                    }
                } else {
                    allMessages.add(ChatPojo(guess, currentPlayer?.playerName));
                }
                runOnUiThread {
                    chatAdapter.notifyDataSetChanged();
                    autoScrollChat()
                }
            })
        }
    }

    private fun autoScrollChat() {
        chatRecycler.post { // Call smooth scroll
            chatRecycler.smoothScrollToPosition(chatAdapter.itemCount - 1)
        }
    }

    private fun generateRandomWords() {
        mSocket?.emit("turnChange", currentPlayer?.rank, Ack {
            val data = it[0] as JSONObject;
            val whoseTurn = data["whoseTurn"] as Int;
            runOnUiThread { //Code for the UiThread
                if (whoseTurn == currentPlayer?.rank) {
                    canDraw = true;
                    canvas.canUserDraw(true)
                    drawingOptionsLayout.visibility = View.VISIBLE;
                } else {
                    canDraw = false;
                    canvas.canUserDraw(false)
                    drawingOptionsLayout.visibility = View.GONE;
                }
            }


        })
    }

    private fun makDrawingViewsVisible() {
        canvas.canUserDraw(true)
        canDraw = true;
        drawingOptionsLayout.visibility = View.VISIBLE;
        Log.d("TurnChange", "Random Words Now visible");
    }
}