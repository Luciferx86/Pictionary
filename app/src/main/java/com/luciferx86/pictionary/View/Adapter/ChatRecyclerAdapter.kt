package com.luciferx86.pictionary.View.Adapter

import android.content.Context
import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.luciferx86.pictionary.Model.ChatPojo
import com.luciferx86.pictionary.R
import kotlin.collections.ArrayList

class ChatRecyclerAdapter(
    context: Context?,
    listMessages: ArrayList<ChatPojo>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mContext: Context? = null
    private var allMessages: ArrayList<ChatPojo>
    override fun onCreateViewHolder(
        viewGroup: ViewGroup, viewType: Int
    ): RecyclerView.ViewHolder {

        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.single_message_layout, viewGroup, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        pos: Int
    ) {
        if (viewHolder is MessageViewHolder) {
            val message: ChatPojo? = allMessages[pos]
            val messageToDisplay = "<b>${message?.messageFrom}</b>: ${message?.messageBody}"
            viewHolder.messageBody.text = Html.fromHtml(messageToDisplay)
            if(message?.messageFrom.equals("Game")){
                viewHolder.messageBody.setTextColor(Color.parseColor("#1fa32c"));
            }
        }
    }

    override fun getItemCount(): Int {
        return allMessages.size
    }

    private inner class MessageViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var messageBody: TextView = view.findViewById(R.id.singleMessageText)

    }

    init {
        mContext = context
        allMessages = listMessages
        allMessages.reverse()
    }
}
