package com.luciferx86.pictionary.View.Activiy

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.luciferx86.pictionary.Model.SinglePlayerPojo
import com.luciferx86.pictionary.R
import com.luciferx86.pictionary.RootApplication
import com.luciferx86.pictionary.RootApplication.mSocket
import io.socket.client.Ack
import io.socket.client.IO
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception


class LaunchpadActivity : AppCompatActivity() {

    lateinit var createGame: Button;
    lateinit var joinGame: Button;
    lateinit var createAvatar: Button;
//    private val mSocket = IO.socket("https://pict-dup.herokuapp.com");

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launchpad)
        initSocket();
        mSocket?.connect();
        initViews();
        setClickListeners();

    }

    private fun initSocket() {
        mSocket = IO.socket("https://pictionary-server.herokuapp.com");
    }

    fun initViews() {
        createGame = findViewById(R.id.createGameButton);
        joinGame = findViewById(R.id.joinGameButton);
        createAvatar = findViewById(R.id.createAvatar);
    }

    private fun setClickListeners() {
        createGame.setOnClickListener {

            Log.d("Create", "Creating Game");
            val createDialog: Dialog = Dialog(this);
            createDialog.setContentView(R.layout.create_game_dialog_layout)

            createDialog.window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            createDialog.findViewById<Button>(R.id.createGameDialogButton)?.setOnClickListener {
                val createGameUsername =
                    createDialog.findViewById<TextView>(R.id.createGameUsername);
                val roundsCountSpinner = createDialog.findViewById<Spinner>(R.id.roundsCountSpinner);
                val username = createGameUsername?.text.toString()
                if (username.length >= 4) {
                    val roundsCount = roundsCountSpinner.selectedItem.toString().toInt();

                    mSocket?.emit("createGame", username, roundsCount, Ack() {
                        val data = it[0] as JSONObject
                        Log.d("GameCreated", "gameCode " + data["gameCode"]);
                        val gameState = data["gameState"] as JSONObject;
                        val i: Intent = Intent(this, MainActivity::class.java);
                        val newPlayer = parsePlayerFromJSON(data["newPlayer"] as JSONObject);
                        Log.d("NewPlayerVal", newPlayer.toString())
                        i.putExtra("gameCode", data["gameCode"] as Int);
                        i.putExtra("gameState", gameState.toString());
                        i.putExtra("newPlayer", newPlayer as Parcelable);
                        i.putExtra("gameCreator", true);
//                        createDialog.dismiss()
                        startActivity(i);
                    });
                } else {
                    Toast.makeText(this, "Username must be 4 characters", Toast.LENGTH_LONG).show();
                }
            }

            createDialog.show()
        }

        joinGame.setOnClickListener {
            Log.d("Join", "Joining Game");
            val joinDialog: Dialog = Dialog(this);
            joinDialog.setContentView(R.layout.join_game_dialog_layout)

            joinDialog.window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            joinDialog.findViewById<Button>(R.id.joinGameNowButton)?.setOnClickListener {
                val joinCodeText = joinDialog.findViewById<TextView>(R.id.gameCode);
                val joinUsernameText = joinDialog.findViewById<TextView>(R.id.usernameJoin);
                val codeText = joinCodeText?.text.toString()
                val username = joinUsernameText?.text.toString()
                if (codeText.length == 4 && username.length >= 4) {
                    mSocket?.emit("joinGame", username, codeText, Ack() {
                        val data = it[0] as JSONObject

                        Log.d("GameJoined", "code " + data["gameState"]);
                        try {
                            val gameState = data["gameState"] as JSONObject;
                            val i = Intent(this, MainActivity::class.java);
                            val newPlayer =
                                parsePlayerFromJSON(data["newPlayer"] as JSONObject);
                            Log.d("NewPlayerVal", newPlayer.toString());
                            val code: String = data["gameCode"] as String;
                            i.putExtra("gameCode", code.toInt());
                            i.putExtra("gameState", gameState.toString());
                            i.putExtra("newPlayer", newPlayer as Parcelable);
                            i.putExtra("gameCreator", false);
                            startActivity(i);

                        } catch (e: Exception) {
                            e.printStackTrace();
                            runOnUiThread {
                                Toast.makeText(this, "Invalid Game Code", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "Code Invalid", Toast.LENGTH_LONG).show();
                }
            }

            joinDialog.show()
        }

        createAvatar.setOnClickListener{
            val i = Intent(this, CharecterGenerationActivity::class.java);
            startActivity(i);
        }
    }

    private fun parsePlayerFromJSON(json: JSONObject): SinglePlayerPojo {
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
}