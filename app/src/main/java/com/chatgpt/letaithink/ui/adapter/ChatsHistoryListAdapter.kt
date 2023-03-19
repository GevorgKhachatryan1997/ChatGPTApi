package com.chatgpt.letaithink.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.chatgpt.letaithink.R
import com.chatgpt.letaithink.model.databaseModels.SessionEntity
import com.chatgpt.letaithink.utils.hideKeyboard
import com.chatgpt.letaithink.utils.showKeyboard

class ChatsHistoryListAdapter : ListAdapter<SessionEntity, ChatsHistoryListAdapter.ChatsHistoryViewHolder>(ChatHistoryItemCallback()) {

    var itemClickListener: OnSessionClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_history_item, parent, false)
        return ChatsHistoryViewHolder(view).also {
            it.itemClickListener = itemClickListener
        }
    }

    override fun onBindViewHolder(holder: ChatsHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ChatsHistoryViewHolder(view: View) : ViewHolder(view) {

        private var session: SessionEntity? = null
        var itemClickListener: OnSessionClickListener? = null

        private val nameContainer: ConstraintLayout = view.findViewById(R.id.name_container)
        private val editNameContainer: ConstraintLayout = view.findViewById(R.id.edit_name_container)
        private val tvName: TextView = view.findViewById(R.id.tv_chat_name)
        private val etName: EditText = view.findViewById(R.id.et_chat_name)
        private val ivDelete: ImageView = view.findViewById(R.id.iv_delete)
        private val ivEdit: ImageView = view.findViewById(R.id.iv_edit)
        private val ivSaveName: ImageView = view.findViewById(R.id.iv_save_name)

        init {
            tvName.setOnClickListener {
                itemClickListener?.onSessionClick(requireSession())
            }

            ivSaveName.setOnClickListener {
                val newName = etName.text.toString()
                if (newName.isNotBlank()) {
                    switchNameView(newName)
                    itemClickListener?.updateSessionName(requireSession(), newName)
                } else {
                    Toast.makeText(view.context, R.string.name_is_empty, Toast.LENGTH_LONG).show()
                }
            }

            ivEdit.setOnClickListener {
                switchNameEditView()
            }

            ivDelete.setOnClickListener {
                itemClickListener?.onSessionDeleteClick(requireSession())
            }
        }

        fun bind(sessionEntity: SessionEntity) {
            session = sessionEntity
            switchNameView(sessionEntity.sessionName)
        }

        private fun requireSession(): SessionEntity = session!!

        private fun switchNameView(text: String? = null) {
            editNameContainer.hideKeyboard()
            editNameContainer.visibility = View.GONE
            nameContainer.visibility = View.VISIBLE

            if (text != null) {
                tvName.text = text
            }
        }

        private fun switchNameEditView() {
            nameContainer.visibility = View.GONE
            editNameContainer.visibility = View.VISIBLE

            etName.apply {
                setText(tvName.text)
                requestFocus()
                showKeyboard()
            }
        }
    }

    class ChatHistoryItemCallback : ItemCallback<SessionEntity>() {
        override fun areItemsTheSame(oldItem: SessionEntity, newItem: SessionEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SessionEntity, newItem: SessionEntity): Boolean {
            return oldItem == newItem
        }
    }

    interface OnSessionClickListener {
        fun onSessionClick(session: SessionEntity)
        fun onSessionDeleteClick(session: SessionEntity)
        fun updateSessionName(session: SessionEntity, name: String)
    }
}