package com.luciferx86.pictionary.View.Activiy

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.luciferx86.pictionary.Model.ChatPojo
import com.luciferx86.pictionary.Model.GameStatePojo
import com.luciferx86.pictionary.Model.SinglePlayerPojo
import com.luciferx86.pictionary.R
import com.luciferx86.pictionary.Utils.ColorDecor
import com.luciferx86.pictionary.Utils.DoodleCanvas
import com.luciferx86.pictionary.View.Adapter.ActivePlayersAdapter
import com.luciferx86.pictionary.View.Adapter.ChatRecyclerAdapter
import com.luciferx86.pictionary.View.Adapter.ColorSelectionAdapter
import io.socket.client.Ack
import io.socket.client.IO
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

    private val mSocket = IO.socket("https://pictionary-server.herokuapp.com");
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews();
        setSocketListeners();
        initGameState();
        setClickListeners();



        initRecyclers();


        mSocket.connect();

        addColors();


//        allPlayers.add("Luciferx86");
//        allPlayers.add("smolbean");


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
        currentWord = findViewById(R.id.currentWord);
        firstWord = findViewById(R.id.firstWord)
        secondWord = findViewById(R.id.secondWord)
        thirdWord = findViewById(R.id.thirdWord)
        colorSelectionRecycler = findViewById(R.id.colorOptionsLayout);
        activePlayersRecycler = findViewById(R.id.activePlayersRecycler);
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

        paintBucket = findViewById(R.id.paintBucket);
        sendMessageButton = findViewById(R.id.sendGuess);

        FirebaseApp.initializeApp(this);
        gameCode = intent.getIntExtra("gameCode", 0);
        gameCodeText.text = gameCode.toString()
        val gameCreator = intent.getBooleanExtra("gameCreator", false);
        if (!gameCreator) {
            startGameButton.visibility = View.GONE;
        }
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

            mSocket.emit("startGame", gameCode, Ack {
                runOnUiThread {
                    canvas.canUserDraw(true)
                    gameInfoLayout.visibility = View.GONE;
                }
            });
        }

        sendMessageButton.setOnClickListener {
            sendGuess();
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
        currentWord.text = "_ ".repeat(value.length).substring(0, 2 * value.length - 1);
        mSocket.emit("wordSelect", value, gameCode, Ack {
            runOnUiThread {
                val data = it[0] as JSONObject;
                val wordHint = data["wordHint"] as String;
                currentWord.text = wordHint;
                Log.d("WordPosted", value);
            }
        })
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
        mSocket.on("joinGame", Emitter.Listener { args ->
            runOnUiThread { //Code for the UiThread
                val data = args[0] as JSONObject
                val newPlayerJson = data["newPlayer"] as JSONObject;
                val newPLayer = parsePlayerFromJSON(newPlayerJson);
                allPlayers.add(newPLayer);
                activePlayersAdapter.notifyDataSetChanged();
            }
        });
        mSocket.on("newMessage", Emitter.Listener { args ->
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
        mSocket.on("turnChange", Emitter.Listener { args ->
            runOnUiThread { //Code for the UiThread
                val data = args[0] as JSONObject
                val whoseTurn = data["whoseTurn"] as Int;
                if (whoseTurn == currentPlayer?.rank) {
                    mSocket.emit("genRandomWords", Ack {
                        runOnUiThread {
                            wordSelectionLayout.visibility = View.VISIBLE;
                            val data = it[0] as JSONObject;
                            val randWords = data["randomWords"] as JSONArray;
                            firstWord.text = randWords[0].toString();
                            secondWord.text = randWords[1].toString();
                            thirdWord.text = randWords[2].toString();

                        }

                    })
                    canvas.canUserDraw(true)
                    canDraw = true;
                    drawingOptionsLayout.visibility = View.VISIBLE;
                } else {
                    canDraw = false;
                    canvas.canUserDraw(false);
                    drawingOptionsLayout.visibility = View.GONE;
                }

            }
        });

        mSocket.on("startGame", Emitter.Listener { args ->
            runOnUiThread { //Code for the UiThread
                canDraw = false;
                canvas.canUserDraw(false);
                drawingOptionsLayout.visibility = View.GONE;
                gameInfoLayout.visibility = View.GONE;
            }


        });

        mSocket.on("wordSelect", Emitter.Listener { args ->
            runOnUiThread { //Code for the UiThread
                val data = args[0] as JSONObject;
                val wordHint = data["wordHint"] as String;
                currentWord.text = wordHint
            }


        });
    }

    private fun sendGuess() {
        if (chatMessageEditText.text.toString().isNotEmpty()) {
            val guess = chatMessageEditText.text.toString();
            chatMessageEditText.text = "";
            mSocket.emit("newMessage", guess, "username", gameCode, Ack {
                val data = it[0] as JSONObject;
                Log.d("NewMessage", it[0].toString());

                val correctGuess = data["wordGuessed"] as Boolean;
                Log.d("NewMessage", correctGuess.toString());
                if (correctGuess) {
                    allMessages.add(ChatPojo("You Guessed the word!", "Game"));
                } else {
                    allMessages.add(ChatPojo(guess, "usrname"));
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
        mSocket.emit("turnChange", currentPlayer?.rank, gameCode, Ack {
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
}