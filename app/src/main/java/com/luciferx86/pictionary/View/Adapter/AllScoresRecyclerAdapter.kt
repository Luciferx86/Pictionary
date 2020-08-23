package com.luciferx86.pictionary.View.Adapter


import android.os.Build
import android.view.LayoutInflater

import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.luciferx86.pictionary.Model.ScoreCard
import com.luciferx86.pictionary.R

/**
 * Created by Luciferx86 on 23/08/20.
 */
class AllScoresRecyclerAdapter(
    private val scoreList: List<ScoreCard>
) : RecyclerView.Adapter<AllScoresRecyclerAdapter.ScoreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ScoreViewHolder(
            inflater,
            parent
        )

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val score = scoreList[position]
        holder.bind(score)
    }

    override fun getItemCount(): Int = scoreList.size

    class ScoreViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(
            inflater.inflate(
                R.layout.player_score_card,
                parent,
                false
            )
        ) {
        private var playerNameView: TextView? = null;
        private var playerScoreView: TextView? = null;

        init {
            playerNameView = itemView.findViewById(R.id.scorePlayerName);
            playerScoreView = itemView.findViewById(R.id.scorePlayerScore);
        }

        fun bind(score: ScoreCard) {
            playerNameView?.text = score.playerName
            playerScoreView?.text = score.score.toString();
        }
    }
}