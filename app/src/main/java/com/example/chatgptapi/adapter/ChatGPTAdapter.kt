package com.example.chatgptapi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.chatgptapi.R
import com.example.chatgptapi.model.UiAiModel

class ChatGPTAdapter : RecyclerView.Adapter<ChatGPTViwHolder>() {
    private val uiAiModels = mutableListOf<UiAiModel>()
    var itemClickListener: OnAiModelClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatGPTViwHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.ui_ai_model, parent, false)
        return ChatGPTViwHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: ChatGPTViwHolder, position: Int) {
        holder.bind(uiAiModels[position])
    }

    override fun getItemCount() = uiAiModels.size

    fun update(newItemsData: List<UiAiModel>) {
        uiAiModels.clear()
        uiAiModels.addAll(newItemsData)
        notifyDataSetChanged()
    }
}

class ChatGPTViwHolder(
    itemView: View,
    private val onAiModelClickListener: OnAiModelClickListener?
) : RecyclerView.ViewHolder(itemView) {

    private val ivModel = itemView.findViewById<ImageView>(R.id.ivAiModel)
    private val tvModelName = itemView.findViewById<TextView>(R.id.tvAiModelName)
    private val tvModelInfo = itemView.findViewById<TextView>(R.id.tvAiModelInfo)
    private val container = itemView.findViewById<ConstraintLayout>(R.id.container)
    private var uiAiModel: UiAiModel? = null

    init {
        container.setOnClickListener {
            uiAiModel?.let {
                onAiModelClickListener?.onClick(it)
            }
        }
    }

    fun bind(uiAiModel: UiAiModel) {
        this.uiAiModel = uiAiModel
        ivModel.setImageResource(uiAiModel.image)
        tvModelName.setText(uiAiModel.name)
        tvModelInfo.setText(uiAiModel.info)
    }
}

fun interface OnAiModelClickListener {
    fun onClick(uIaIModel: UiAiModel)
}