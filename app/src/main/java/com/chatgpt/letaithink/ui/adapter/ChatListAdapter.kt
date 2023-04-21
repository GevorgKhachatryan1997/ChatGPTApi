package com.chatgpt.letaithink.ui.adapter

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.chatgpt.letaithink.R
import com.chatgpt.letaithink.data.*
import com.chatgpt.letaithink.utils.addToClipboard
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class ChatListAdapter : ListAdapter<ConversationItem, ChatListAdapter.ConversationViewHolder<ConversationItem>>(ChatDiffUtilCallback()) {

    companion object {
        private const val VIEW_TYPE_USER = 1000
        private const val VIEW_TYPE_AI_THINKING = 1001
        private const val VIEW_TYPE_AI_TEXT = 1002
        private const val VIEW_TYPE_AI_CHAT_TEXT = 1003
        private const val VIEW_TYPE_AI_IMAGE = 1004
    }

    var onChatListener: ChatListListener? = null

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
            is AiChatMessage -> VIEW_TYPE_AI_CHAT_TEXT
            is AiImage -> VIEW_TYPE_AI_IMAGE
        }
    }

    @Throws(IllegalArgumentException::class)
    @LayoutRes
    private fun getItemLayout(viewType: Int): Int = when (viewType) {
        VIEW_TYPE_USER -> R.layout.user_question_item
        VIEW_TYPE_AI_THINKING -> R.layout.ai_answer_item
        VIEW_TYPE_AI_TEXT, VIEW_TYPE_AI_CHAT_TEXT -> R.layout.ai_answer_item
        VIEW_TYPE_AI_IMAGE -> R.layout.ai_image_item
        else -> throw IllegalArgumentException("View type not supported: $viewType")
    }

    private fun createViewHolder(viewType: Int, view: View): ConversationViewHolder<*> {
        return when (viewType) {
            VIEW_TYPE_USER -> UserMessageViewHolder(view)
            VIEW_TYPE_AI_THINKING -> AiThinkingViewHolder(view)
            VIEW_TYPE_AI_TEXT -> AiMessageViewHolder(view)
            VIEW_TYPE_AI_CHAT_TEXT -> AiChatMessageViewHolder(view)
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

    abstract class ConversationViewHolder<in T : ConversationItem>(view: View) : ViewHolder(view) {

        abstract fun bind(message: T)
    }

    inner class UserMessageViewHolder(view: View) : ConversationViewHolder<UserMessage>(view) {
        private val tvText: TextView = view.findViewById(R.id.tvText)
        private val icEdit: ImageView = view.findViewById(R.id.ivEditMessage)

        private var userMessage: UserMessage? = null

        init {
            icEdit.setOnClickListener {
                onChatListener?.onEditClick(userMessage!!)
            }
        }

        override fun bind(message: UserMessage) {
            userMessage = message
            tvText.text = message.message
        }
    }

    class AiThinkingViewHolder(view: View) : ConversationViewHolder<AiThinking>(view) {

        private val tvText: TextView = view.findViewById(R.id.tvText)
        private val ivCopy: ImageView = view.findViewById(R.id.ivCopyMessage)

        init {
            ivCopy.visibility = View.GONE
        }

        override fun bind(message: AiThinking) {
            tvText.text = message.message
        }
    }

    inner class AiMessageViewHolder(view: View) : ConversationViewHolder<AiMessage>(view) {

        private val tvText: TextView = view.findViewById(R.id.tvText)
        private val icCopy: ImageView = view.findViewById(R.id.ivCopyMessage)

        init {
            icCopy.setOnClickListener {
                view.context.addToClipboard(tvText.text.toString())
            }
        }

        override fun bind(message: AiMessage) {
            val text = StringBuilder()
            message.textCompletion.choices?.forEach {
                text.append(it.text)
            }

            tvText.text = text
        }
    }

    inner class AiChatMessageViewHolder(view: View) : ConversationViewHolder<AiChatMessage>(view) {

        private val tvText: TextView = view.findViewById(R.id.tvText)
        private val icCopy: ImageView = view.findViewById(R.id.ivCopyMessage)

        init {
            icCopy.setOnClickListener {
                view.context.addToClipboard(tvText.text.toString())
            }
        }

        override fun bind(message: AiChatMessage) {
            val text = StringBuilder()
            message.chatCompletion.choices?.first()?.message?.let {
                text.append(it.content)
            }

            tvText.text = text
        }
    }

    inner class AiImageViewHolder(view: View) : ConversationViewHolder<AiImage>(view) {

        private val ivImage: ImageView = view.findViewById(R.id.ivImage)
        private val ivError: ImageView = view.findViewById(R.id.iv_error)
        private val pbLoading: ProgressBar = view.findViewById(R.id.pbLoading)
        private val cvDownload: CardView = view.findViewById(R.id.cvDownload)

        private var aiImage: AiImage? = null

        init {
            cvDownload.setOnClickListener {
                val bitmap = (ivImage.drawable as BitmapDrawable).bitmap
                onChatListener?.onDownloadClick(bitmap)
            }
        }

        override fun bind(message: AiImage) {
            aiImage = message

            pbLoading.visibility = View.VISIBLE
            cvDownload.visibility = View.GONE
            val imageUri = message.image.data?.first()
            if (imageUri == null) {
                onImageLoadFail()
            } else {
                Picasso.get().load(Uri.parse(imageUri.url)).into(ivImage, object : Callback {
                    override fun onSuccess() {
                        onSuccessImageLoad()
                    }

                    override fun onError(e: Exception?) {
                        onImageLoadFail()
                    }
                })
            }
        }

        private fun onSuccessImageLoad() {
            pbLoading.visibility = View.GONE
            cvDownload.visibility = View.VISIBLE
            ivError.visibility = View.GONE
        }

        private fun onImageLoadFail() {
            pbLoading.visibility = View.GONE
            cvDownload.visibility = View.GONE
            ivError.visibility = View.VISIBLE
        }
    }

    interface ChatListListener {
        fun onDownloadClick(image: Bitmap)
        fun onEditClick(message: UserMessage)
    }
}