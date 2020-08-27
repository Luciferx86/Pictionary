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
import com.google.gson.JsonObject
import com.luciferx86.pictionary.Model.AvatarState
import com.luciferx86.pictionary.Model.SinglePlayerPojo
import com.luciferx86.pictionary.Utils.OnAvatarUploadCompleteListener
import com.luciferx86.pictionary.R
import com.luciferx86.pictionary.RootApplication.mSocket
import io.socket.client.Ack
import io.socket.client.IO
import kotlinx.android.synthetic.main.avatar_layout.*
import kotlinx.android.synthetic.main.avatar_layout.eyes
import kotlinx.android.synthetic.main.avatar_layout.face
import kotlinx.android.synthetic.main.avatar_layout.hat
import kotlinx.android.synthetic.main.avatar_layout.mouth
import kotlinx.android.synthetic.main.single_player_card_layout.*
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

    var myAvatarState: AvatarState = AvatarState(-1914910, 0, 0, -1);

    lateinit var sharedPreferences: SharedPreferences;


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launchpad)
        initSocket();
        initSharedPrefs()
        mSocket?.connect();

        initAvatarState();
        initViews();
        setClickListeners();
        loadStoredUsername();
    }

    private fun loadAvatar() {

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initAvatarState() {
        val avatarColor = sharedPreferences.getInt("avatarColor", -1);
        val hatIndex = sharedPreferences.getInt("hatIndex", -1);
        val eyesIndex = sharedPreferences.getInt("eyesIndex", -1);
        val mouthIndex = sharedPreferences.getInt("mouthIndex", -1);
        if (avatarColor != -1 && hatIndex != -1 && eyesIndex != -1 && mouthIndex != -1) {
            myAvatarState = AvatarState(avatarColor, hatIndex, eyesIndex, mouthIndex);
            setAvatarColor(myAvatarState!!.faceColor);
            setAvatarEyes(eyesIndex);
            setAvatarMouth(mouthIndex);
            setAvatarHat(hatIndex);
        }

    }

    private fun setAvatarEyes(index: Int) {
        val imgs: TypedArray = resources.obtainTypedArray(R.array.allEyes);
        if (index == -1 || index >= imgs.length()) {
            val random = Random();
            val randomIndex = random.nextInt(imgs.length());
            eyes.background = imgs.getDrawable(randomIndex);
            myAvatarState.eyesIndex = randomIndex;
        } else {
            eyes.background = imgs.getDrawable(index);
            myAvatarState.eyesIndex = index;
        }
    }

    private fun setAvatarMouth(index: Int) {
        val imgs: TypedArray = resources.obtainTypedArray(R.array.allMouths);
        if (index == -1 || index >= imgs.length()) {
            val random = Random();
            val randomIndex = random.nextInt(imgs.length());
            mouth.background = imgs.getDrawable(randomIndex);
            myAvatarState.mouthIndex = randomIndex;
        } else {
            mouth.background = imgs.getDrawable(index);
            myAvatarState.mouthIndex = index;
        }

    }

    private fun setAvatarHat(index: Int) {
        val imgs: TypedArray = resources.obtainTypedArray(R.array.allHats);
        if (index == -1 || index >= imgs.length()) {
            val random = Random();
            val randomIndex = random.nextInt(imgs.length());
            hat.background = imgs.getDrawable(randomIndex);
            myAvatarState.hatIndex = randomIndex;
        } else {
            hat.background = imgs.getDrawable(index);
            myAvatarState.hatIndex = index;
        }
    }

    private fun initSocket() {
        mSocket = IO.socket("https://pictionary-server.herokuapp.com");
    }

    private fun initSharedPrefs() {
        sharedPreferences = this.getSharedPreferences(sharedPrefsTag, Context.MODE_PRIVATE)
    }

    private fun initViews() {
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

    private fun getAvatarState(): AvatarState {
        return myAvatarState;
    }

    private fun getAvatarJson(avatar: AvatarState): JSONObject {
        val obj = JSONObject()
        obj.put("faceColor", avatar.faceColor);
        obj.put("eyesIndex", avatar.eyesIndex);
        obj.put("mouthIndex", avatar.mouthIndex);
        obj.put("hatIndex", avatar.hatIndex);
        return obj;
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setClickListeners() {
        createGame.setOnClickListener {
            if (validateUsername()) {
                Log.d("Create", "Creating Game");
                val createDialog: Dialog = Dialog(this@LaunchpadActivity);
                createDialog.setContentView(R.layout.create_game_dialog_layout)

                createDialog.window?.apply {
                    setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
                    setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                }
                createDialog.findViewById<Button>(R.id.createGameDialogButton)
                    ?.setOnClickListener {

                        val roundsCountSpinner =
                            createDialog.findViewById<Spinner>(R.id.roundsCountSpinner);
                        val username = usernameField.text.toString()
                        if (username.length >= 4) {
                            saveUsernameToStorage(username);
                            saveAvatarToStorage();
                            val roundsCount =
                                roundsCountSpinner.selectedItem.toString().toInt();

                            mSocket?.emit(
                                "createGame",
                                username,
                                getAvatarJson(myAvatarState),
                                roundsCount,
                                Ack() {

                                    val data = it[0] as JSONObject
                                    Log.d("GameCreated", "gameCode " + data["gameCode"]);
                                    val gameState = data["gameState"] as JSONObject;
                                    val i: Intent =
                                        Intent(
                                            this@LaunchpadActivity,
                                            MainActivity::class.java
                                        );
                                    val newPlayer =
                                        parsePlayerFromJSON(data["newPlayer"] as JSONObject);
                                    Log.d("NewPlayerVal", newPlayer.toString())
                                    i.putExtra("gameCode", data["gameCode"] as Int);
                                    i.putExtra("gameState", gameState.toString());
                                    i.putExtra("newPlayer", newPlayer as Parcelable);
                                    i.putExtra("gameCreator", true);
//                        createDialog.dismiss()
                                    startActivity(i);
                                });

                        } else {
                            Toast.makeText(
                                this@LaunchpadActivity,
                                "Username must be 4 characters",
                                Toast.LENGTH_LONG
                            )
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
                        mSocket?.emit("joinGame", username, getAvatarJson(myAvatarState), codeText, Ack() {
                            saveUsernameToStorage(username);
                            saveAvatarToStorage();
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

    private fun getBitmapFromView(view: View): Bitmap {
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
        inImage.compress(Bitmap.CompressFormat.PNG, 30, bytes)
        val path: String = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    private fun saveBitmapToFirebase(
        gameCode: Int,
        avatarImage: Bitmap,
        onComplete: OnAvatarUploadCompleteListener
    ) {
        val profileImageRef: StorageReference = FirebaseStorage.getInstance()
            .getReference("avatars/" + gameCode.toString() + "/" + System.currentTimeMillis() + ".png")

        profileImageRef.putFile(getImageUri(this, avatarImage))
            .addOnSuccessListener {
                // profileImageUrl taskSnapshot.getDownloadUrl().toString(); //this is depreciated

                //this is the new way to do it
                profileImageRef.downloadUrl
                    .addOnCompleteListener { task ->
                        val profileImageUrl: String = task.result.toString()
                        Log.i("URL", profileImageUrl)
                        onComplete.avatarUploadComplete(profileImageUrl);
                    }
            }
            .addOnFailureListener { e ->
//                progressBar.setVisibility(View.GONE)
                Toast.makeText(baseContext, "aaa " + e.message, Toast.LENGTH_SHORT)
                    .show()
            }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setAvatarColor(color: Int) {
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
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun randomizeMyAvatar() {
        val color = getRandomColor();
        setAvatarColor(color);
        myAvatarState.faceColor = color;

        setAvatarEyes(-1);

        setAvatarMouth(-1);

        setAvatarHat(-1);

//        saveImage(getBitmapFromView(avatarLayout));
    }

    private fun saveAvatarToStorage() {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt("avatarColor", myAvatarState!!.faceColor)
        editor.putInt("hatIndex", myAvatarState!!.hatIndex)
        editor.putInt("eyesIndex", myAvatarState!!.eyesIndex)
        editor.putInt("mouthIndex", myAvatarState!!.mouthIndex);

        editor.apply()
        editor.commit()
    }

    private fun getRandomColor(): Int {
        val obj = Random()
        return Color.rgb(obj.nextInt(154) + 100, obj.nextInt(154) + 100, obj.nextInt(154) + 100);
    }

    private fun loadStoredUsername() {
        val username = sharedPreferences.getString("username", "");
        usernameField.setText(username);
    }

    fun parseAvatarFromJSON(json: JSONObject): AvatarState {
        val faceColor = json["faceColor"] as Int;
        val eyesIndex = json["eyesIndex"] as Int;
        val mouthIndex = json["mouthIndex"] as Int;
        val hatIndex = json["hatIndex"] as Int;
        return AvatarState(-faceColor, hatIndex, eyesIndex, mouthIndex);
    }

    private fun parsePlayerFromJSON(json: JSONObject): SinglePlayerPojo {
        var playerName: String? = null
        var playerScore: String? = null
        var playerRank: String? = null
        var playerAvatar: AvatarState? = null
        try {
            playerName = json.getString("playerName")
            playerScore = json.getString("score")
            playerRank = json.getString("rank")
            playerAvatar = parseAvatarFromJSON(JSONObject(json.getString("playerAvatar")));
            val newPlayer = SinglePlayerPojo(
                playerName,
                playerScore.toInt(),
                playerRank.toInt(),
                playerAvatar!!
            );
            return newPlayer;
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return SinglePlayerPojo();
    }
}