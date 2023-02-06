package com.example.chatgptapi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatgptapi.R
import com.example.chatgptapi.model.UiAiModel

class GPTAdapter : RecyclerView.Adapter<GPTViwHolder>() {
    private val dataItem = mutableListOf<UiAiModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GPTViwHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.ui_ai_model, parent, false)
        return GPTViwHolder(view)
    }

    override fun onBindViewHolder(holder: GPTViwHolder, position: Int) {
        holder.bind(dataItem[position])
    }

    override fun getItemCount() = dataItem.size

    fun update(newItemsData: List<UiAiModel>) {
        dataItem.addAll(newItemsData)
        notifyDataSetChanged()
    }
}

class GPTViwHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val modelImageView = itemView.findViewById<ImageView>(R.id.aiModelImageView)
    private val modelNameTextView = itemView.findViewById<TextView>(R.id.tvAiModelName)
    private val modelInfoTextView = itemView.findViewById<TextView>(R.id.tvAiModelInfo)

    fun bind(uiAiModel: UiAiModel) {
        modelImageView.setImageResource(uiAiModel.image)
        modelNameTextView.setText(uiAiModel.name)
        modelInfoTextView.setText(uiAiModel.info)
    }
}