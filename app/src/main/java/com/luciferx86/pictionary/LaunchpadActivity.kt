package com.luciferx86.pictionary

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject


class LaunchpadActivity : AppCompatActivity() {

    lateinit var createGame: Button;
    lateinit var joinGame: Button;
    private val mSocket = IO.socket("https://pictionary-server.herokuapp.com");
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launchpad)
        mSocket.connect();
        initViews();
        setClickListeners();
    }

    fun initViews() {
        createGame = findViewById(R.id.createGameButton);
        joinGame = findViewById(R.id.joinGameButton);

    }

    fun setClickListeners() {
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
                val username = createGameUsername?.text.toString()
                if (username.length >= 4) {
                    mSocket.emit("createGame", username, Ack() {
                        val data = it[0] as JSONObject
                        Log.d("GameCreated", "code " + data["code"]);
                        val gameState = data["gameState"] as JSONObject;
                        val i: Intent = Intent(this, MainActivity::class.java);
                        i.putExtra("gameCode", data["code"] as Int);
                        i.putExtra("gameState", gameState.toString());
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
                    mSocket.emit("joinGame", username, codeText, Ack() {
                        val data = it[0] as JSONObject

                        Log.d("GameJoined", "code " + data["gameState"]);
                        val gameState = data["gameState"] as JSONObject;
                        val i: Intent = Intent(this, MainActivity::class.java);
                        i.putExtra("gameCode", data["code"] as String);
                        i.putExtra("gameState", gameState.toString());
                        startActivity(i);
                    });
                } else {
                    Toast.makeText(this, "Code Invalid", Toast.LENGTH_LONG).show();
                }
            }

            joinDialog.show()
        }
    }
}