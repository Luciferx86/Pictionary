package com.luciferx86.pictionary.View.Adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.luciferx86.pictionary.R

class ColorSelectionAdapter(
    private val colorList: List<Int>, private val adapterOnClick: (Int) -> Unit
) : RecyclerView.Adapter<ColorSelectionAdapter.ColorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ColorViewHolder(
            inflater,
            parent
        )

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
        private var colorCircleInner: View? = null;
        private var colorCircleOuter: View? = null;

        init {
            colorCircleInner = itemView.findViewById(R.id.colorCircleInner);
            colorCircleOuter = itemView.findViewById(R.id.colorCircleOuter);
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bind(color: Int, onClick: (Int) -> Unit) {

            colorCircleInner?.backgroundTintList = ColorStateList.valueOf(color);
            colorCircleOuter?.backgroundTintList = ColorStateList.valueOf(manipulateColor(color, 0.7f));
            itemView?.setOnClickListener { onClick(color); }
        }

        fun manipulateColor(color: Int, factor: Float): Int {
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

    }



}