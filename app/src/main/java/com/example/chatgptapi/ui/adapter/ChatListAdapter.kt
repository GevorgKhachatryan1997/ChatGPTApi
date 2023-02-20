package com.example.chatgptapi.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.chatgptapi.R
import com.example.chatgptapi.data.*
import com.example.chatgptapi.ui.ChatViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class ChatListAdapter : ListAdapter<ConversationItem, ChatListAdapter.ConversationViewHolder<ConversationItem>>(ChatDiffUtilCallback()) {

    companion object {
        private const val VIEW_TYPE_USER = 1000
        private const val VIEW_TYPE_AI_THINKING = 1001
        private const val VIEW_TYPE_AI_TEXT = 1002
        private const val VIEW_TYPE_AI_IMAGE = 1003
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder<ConversationItem> {
        val view = LayoutInflater.from(parent.context).inflate(getItemLayout(viewType), parent, false)
        return createViewHolder(viewType, view) as ConversationViewHolder<ConversationItem>
    }

    override fun onBindViewHolder(holder: ConversationViewHolder<ConversationItem>, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UserMessage -> VIEW_TYPE_USER
            is AiThinking -> VIEW_TYPE_AI_THINKING
            is AiMessage -> VIEW_TYPE_AI_TEXT
            is AiImage -> VIEW_TYPE_AI_IMAGE
        }
    }

    @Throws(IllegalArgumentException::class)
    @LayoutRes
    private fun getItemLayout(viewType: Int): Int = when (viewType) {
        VIEW_TYPE_USER -> R.layout.user_question_item
        VIEW_TYPE_AI_THINKING -> R.layout.user_question_item
        VIEW_TYPE_AI_TEXT -> R.layout.ai_answer_item
        VIEW_TYPE_AI_IMAGE -> R.layout.ai_image_item
        else -> throw IllegalArgumentException("View type not supported: $viewType")
    }

    private fun createViewHolder(viewType: Int, view: View): ConversationViewHolder<*> {
        return when(viewType) {
            VIEW_TYPE_USER -> UserMessageViewHolder(view)
            VIEW_TYPE_AI_THINKING -> AiThinkingViewHolder(view)
            VIEW_TYPE_AI_TEXT -> AiMessageViewHolder(view)
            VIEW_TYPE_AI_IMAGE -> AiImageViewHolder(view)
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    class ChatDiffUtilCallback : DiffUtil.ItemCallback<ConversationItem>() {
        override fun areItemsTheSame(oldItem: ConversationItem, newItem: ConversationItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ConversationItem, newItem: ConversationItem): Boolean {
            return oldItem == newItem
        }
    }

    abstract class ConversationViewHolder<in T: ConversationItem>(view: View): ViewHolder(view) {

        abstract fun bind(message: T)
    }

    class UserMessageViewHolder(view: View) : ConversationViewHolder<UserMessage>(view) {
        private val tvText: TextView = view.findViewById(R.id.tvText)

        override fun bind(message: UserMessage) {
            tvText.text = message.message
        }
    }

    class AiThinkingViewHolder(view: View) : ConversationViewHolder<AiThinking>(view) {

        private val tvText: TextView = view.findViewById(R.id.tvText)

        override fun bind(message: AiThinking) {
            tvText.text = message.message
        }
    }

    class AiMessageViewHolder(view: View) : ConversationViewHolder<AiMessage>(view) {

        private val tvText: TextView = view.findViewById(R.id.tvText)

        override fun bind(message: AiMessage) {
            val text = StringBuilder()
            message.textCompletion?.choices?.forEach {
                text.append(it.text)
            }

            tvText.text = text
        }
    }


    class AiImageViewHolder(view: View) : ConversationViewHolder<AiImage>(view) {

        private val ivImage: ImageView = view.findViewById(R.id.ivImage)
        private val pbLoading: ProgressBar = view.findViewById(R.id.pbLoading)

        override fun bind(imageModel: AiImage) {
            pbLoading.visibility = View.VISIBLE
            val imageUri = imageModel.image.data?.first()
            // TODO Handle null case
            Picasso.get().load(Uri.parse(imageUri?.url)).into(ivImage, object : Callback {
                override fun onSuccess() {
                    pbLoading.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    pbLoading.visibility = View.GONE
                }

            });
        }
    }
}