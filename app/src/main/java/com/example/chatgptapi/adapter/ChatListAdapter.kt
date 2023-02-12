package com.example.chatgptapi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.chatgptapi.R
import com.example.chatgptapi.ui.ChatViewModel

class ChatListAdapter : ListAdapter<ChatViewModel.Conversation, ChatListAdapter.QuestionViewHolder>(ChatDiffUtilCallback()) {

    companion object {
        private const val VIEW_TYPE_USER = 1000
        private const val VIEW_TYPE_AI = 1001
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getItemLayout(viewType), parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(getItem(position))
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

    class ChatDiffUtilCallback : DiffUtil.ItemCallback<ChatViewModel.Conversation>() {
        override fun areItemsTheSame(oldItem: ChatViewModel.Conversation, newItem: ChatViewModel.Conversation): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ChatViewModel.Conversation, newItem: ChatViewModel.Conversation): Boolean {
            return oldItem == newItem
        }
    }

    // TODO create separate view holder for each type
    class QuestionViewHolder(view: View) : ViewHolder(view) {

        val tvText: TextView = view.findViewById(R.id.tvText)

        fun bind(conversation: ChatViewModel.Conversation) {
            tvText.text = conversation.completionRequest.prompt + "\n" + conversation.textCompletion?.choices?.first()?.text ?: ""
        }
    }
}