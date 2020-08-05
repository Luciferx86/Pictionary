package com.luciferx86.pictionary.View.Adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.luciferx86.pictionary.R
import com.luciferx86.pictionary.Model.SinglePlayerPojo

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
        private var playerName: TextView? = null;
        private var playerScore: TextView? = null;
        private var playerRank: TextView? = null;
        private var playerAvatar: ImageView? = null;

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
            this.playerRank?.text = "#" + player.rank.toString()
        }

    }

}