package com.luciferx86.pictionary

import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView

class ColorSelectionAdapter(
    private val colorList: List<Int>, private val adapterOnClick: (Int) -> Unit
) : RecyclerView.Adapter<ColorSelectionAdapter.ColorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ColorViewHolder(inflater, parent)

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val color = colorList[position]
        holder.bind(color, adapterOnClick)
    }

    override fun getItemCount(): Int = colorList.size

    class ColorViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(
            inflater.inflate(
                R.layout.single_color_option_layout,
                parent,
                false
            )
        ) {
        private var colorCircle: View? = null;

        init {
            colorCircle = itemView.findViewById(R.id.colorCircle);
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bind(color: Int, onClick: (Int) -> Unit) {

            colorCircle?.backgroundTintList = ColorStateList.valueOf(color);
            colorCircle?.setOnClickListener { onClick(color); }
        }

    }

}