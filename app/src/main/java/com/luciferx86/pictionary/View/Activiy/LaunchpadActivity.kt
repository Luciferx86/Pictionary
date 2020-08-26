package com.luciferx86.pictionary.View.Activiy

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.luciferx86.pictionary.Model.SinglePlayerPojo
import com.luciferx86.pictionary.OnAvatarUploadCompleteListener
import com.luciferx86.pictionary.R
import com.luciferx86.pictionary.RootApplication.mSocket
import io.socket.client.Ack
import io.socket.client.IO
import kotlinx.android.synthetic.main.avatar_layout.*
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*


class LaunchpadActivity : AppCompatActivity() {

    private val sharedPrefsTag = "PictionarySharedPrefs"

    lateinit var createGame: Button;
    lateinit var joinGame: Button;
    lateinit var randomizeAvatar: ImageButton;
    lateinit var avatarlayout: ConstraintLayout;
    lateinit var usernameField: EditText;

    lateinit var sharedPreferences: SharedPreferences;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launchpad)
        initSocket();
        initSharedPrefs()
        mSocket?.connect();
        initViews();
        setClickListeners();
        loadStoredUsername();
    }

    private fun initSocket() {
        mSocket = IO.socket("https://pictionary-server.herokuapp.com");
    }

    private fun initSharedPrefs() {
        sharedPreferences = this.getSharedPreferences(sharedPrefsTag, Context.MODE_PRIVATE)
    }

    fun initViews() {
        createGame = findViewById(R.id.createGameButton);
        joinGame = findViewById(R.id.joinGameButton);
        randomizeAvatar = findViewById(R.id.randomizeAvatar);
        avatarlayout = findViewById(R.id.avatarLayout);
        usernameField = findViewById(R.id.userNameField);
    }

    private fun validateUsername(): Boolean {
        val username = usernameField.text.toString();
        if (username.length > 2) {
            return true;
        }
        Toast.makeText(this, "Username must be atleast 3 chars", Toast.LENGTH_LONG).show();
        return false;
    }

    private fun setClickListeners() {
        createGame.setOnClickListener {
            if (validateUsername()) {
                Log.d("Create", "Creating Game");
                val createDialog: Dialog = Dialog(this);
                createDialog.setContentView(R.layout.create_game_dialog_layout)

                createDialog.window?.apply {
                    setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
                    setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                }

                createDialog.findViewById<Button>(R.id.createGameDialogButton)?.setOnClickListener {

                    val roundsCountSpinner =
                        createDialog.findViewById<Spinner>(R.id.roundsCountSpinner);
                    val username = usernameField?.text.toString()
                    if (username.length >= 4) {
                        saveUsernameToStorage(username);
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
                        Toast.makeText(this, "Username must be 4 characters", Toast.LENGTH_LONG)
                            .show();
                    }
                }

                createDialog.show()
            }
        }

        joinGame.setOnClickListener {
            if (validateUsername()) {
                Log.d("Join", "Joining Game");
                val joinDialog: Dialog = Dialog(this);
                joinDialog.setContentView(R.layout.join_game_dialog_layout)

                joinDialog.window?.apply {
                    setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
                    setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                }

                joinDialog.findViewById<Button>(R.id.joinGameNowButton)?.setOnClickListener {
                    val joinCodeText = joinDialog.findViewById<TextView>(R.id.gameCode);
                    val codeText = joinCodeText?.text.toString()
                    val username = usernameField?.text.toString()
                    if (codeText.length == 4 && username.length >= 4) {
                        mSocket?.emit("joinGame", username, codeText, Ack() {
                            saveUsernameToStorage(username);
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
                                    Toast.makeText(this, "Invalid Game Code", Toast.LENGTH_LONG)
                                        .show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(this, "Code Invalid", Toast.LENGTH_LONG).show();
                    }
                }

                joinDialog.show()
            }
        }



        randomizeAvatar.setOnClickListener {
//            var face: View = avatarLayout.findViewById(R.id.face);
//            var body: View = avatarLayout.findViewById(R.id.body);
//            var rightLeg: View = avatarLayout.findViewById(R.id.rightLeg);
//            var leftLeg: View = avatarLayout.findViewById(R.id.leftLeg);
//            var rightArm: View = avatarLayout.findViewById(R.id.rightArm);
//            var leftArm: View = avatarLayout.findViewById(R.id.leftArm);

            randomizeMyAvatar();
        }
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

    private fun saveUsernameToStorage(username: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.apply()
        editor.commit()
    }

    private fun randomizeEyes() {
        val imgs: TypedArray = resources.obtainTypedArray(R.array.allEyes);
        val random = Random();
        val index = random.nextInt(imgs.length());
        eyes.background = imgs.getDrawable(index);
    }

    private fun randomizeMouth() {
        val imgs: TypedArray = resources.obtainTypedArray(R.array.allMouths);
        val random = Random();
        val index = random.nextInt(imgs.length());
        mouth.background = imgs.getDrawable(index);
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

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    private fun saveBitmapToFirebase(avatarImage: Bitmap, onComplete: OnAvatarUploadCompleteListener) {
        val profileImageRef: StorageReference = FirebaseStorage.getInstance()
            .getReference("profilepics/" + System.currentTimeMillis() + ".jpg")

        profileImageRef.putFile(getImageUri(this, avatarImage))
            .addOnSuccessListener {
                // profileImageUrl taskSnapshot.getDownloadUrl().toString(); //this is depreciated

                //this is the new way to do it
                profileImageRef.downloadUrl
                    .addOnCompleteListener { task ->
                        val profileImageUrl: String = task.result.toString()
                        Log.i("URL", profileImageUrl)
                        onComplete.avatarUploadComplete();
                    }
            }
            .addOnFailureListener { e ->
                progressBar.setVisibility(View.GONE)
                Toast.makeText(baseContext, "aaa " + e.message, Toast.LENGTH_SHORT)
                    .show()
            }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun randomizeMyAvatar() {
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

        randomizeMouth();

//        saveImage(getBitmapFromView(avatarLayout));
    }

    private fun getRandomColor(): Int {
        val obj = Random()
        return Color.rgb(obj.nextInt(154) + 100, obj.nextInt(154) + 100, obj.nextInt(154) + 100);
    }

    fun loadStoredUsername() {
        val username = sharedPreferences.getString("username", "");
        usernameField.setText(username);
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