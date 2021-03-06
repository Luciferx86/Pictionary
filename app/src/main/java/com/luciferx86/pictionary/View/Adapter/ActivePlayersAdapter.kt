package com.luciferx86.pictionary.View.Adapter

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.luciferx86.pictionary.R
import com.luciferx86.pictionary.Model.SinglePlayerPojo
import kotlinx.android.synthetic.main.avatar_layout.*
import java.util.*

class ActivePlayersAdapter(
    private val playerList: List<SinglePlayerPojo>
) : RecyclerView.Adapter<ActivePlayersAdapter.PlayerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PlayerViewHolder(
            inflater,
            parent
        )

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = playerList[position]
        holder.bind(player)
    }

    override fun getItemCount(): Int = playerList.size

    class PlayerViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(
            inflater.inflate(
                R.layout.single_player_card_layout,
                parent,
                false
            )
        ) {
        val context: Context = parent.context;

        private var playerName: TextView? = null;
        private var playerScore: TextView? = null;
        private var playerRank: TextView? = null;
        private var playerAvatar: ConstraintLayout? = null;

        init {
            playerName = itemView.findViewById(R.id.playerName);
            playerScore = itemView.findViewById(R.id.playerScore);
            playerRank = itemView.findViewById(R.id.playerRank);
            playerAvatar = itemView.findViewById(R.id.playerAvatar);
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bind(player: SinglePlayerPojo) {

            this.playerName?.text = player.playerName;
            this.playerScore?.text = player.score.toString();
            this.playerRank?.text = "#" + (player.rank + 1).toString()
            this.playerAvatar?.findViewById<View>(R.id.face)?.backgroundTintList =
                ColorStateList.valueOf(player.playerAvatar.faceColor);
            setAvatarEyes(player.playerAvatar.eyesIndex);
            setAvatarMouth(player.playerAvatar.mouthIndex);
            setAvatarHat(player.playerAvatar.hatIndex);

        }

        private fun setAvatarEyes(index: Int) {
            val imgs: TypedArray = context.resources.obtainTypedArray(R.array.allEyes);
            this.playerAvatar?.findViewById<View>(R.id.eyes)?.background = imgs.getDrawable(index);
        }

        private fun setAvatarMouth(index: Int) {
            val imgs: TypedArray = context.resources.obtainTypedArray(R.array.allMouths);
            this.playerAvatar?.findViewById<View>(R.id.mouth)?.background = imgs.getDrawable(index);
        }

        private fun setAvatarHat(index: Int) {
            val imgs: TypedArray = context.resources.obtainTypedArray(R.array.allHats);
            this.playerAvatar?.findViewById<View>(R.id.hat)?.background = imgs.getDrawable(index);
        }

    }

}