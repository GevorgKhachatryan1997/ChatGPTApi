package com.example.chatgptapi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.chatgptapi.R

class ChatModel

class ChatListAdapter : ListAdapter<ChatModel, ChatListAdapter.ChatViewHolder>(ChatDiffUtilCallback()) {

    companion object {
        private const val VIEW_TYPE_USER = 1000
        private const val VIEW_TYPE_AI = 1001
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getItemLayout(viewType), parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) VIEW_TYPE_USER else VIEW_TYPE_AI
    }

    @Throws(IllegalArgumentException::class)
    @LayoutRes
    private fun getItemLayout(viewType: Int): Int = when (viewType) {
        VIEW_TYPE_USER -> R.layout.user_question_item
        VIEW_TYPE_AI -> R.layout.ai_answer_item
        else -> throw IllegalArgumentException("View type not supported: $viewType")
    }

    class ChatDiffUtilCallback : DiffUtil.ItemCallback<ChatModel>() {
        override fun areItemsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            TODO("Not yet implemented")
        }

        override fun areContentsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            TODO("Not yet implemented")
        }
    }

    class ChatViewHolder(view: View) : ViewHolder(view) {

        fun bind() {}
    }
}