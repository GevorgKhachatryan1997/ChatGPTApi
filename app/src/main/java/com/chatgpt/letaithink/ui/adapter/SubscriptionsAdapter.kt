package com.chatgpt.letaithink.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.ProductDetails
import com.chatgpt.letaithink.R

class SubscriptionsAdapter : RecyclerView.Adapter<SubscriptionsAdapter.SubscriptionViewHolder>() {

    private var subscriptions = ArrayList<ProductDetails>()

    var onItemClickListener: OnSubscriptionClickListener? = null

    fun updateData(productDetails: List<ProductDetails>) {
        with(subscriptions) {
            clear()
            addAll(productDetails)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.subscription_item, parent, false)
        return SubscriptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        holder.bind(subscriptions[position])
    }

    override fun getItemCount(): Int = subscriptions.size

    inner class SubscriptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var productDetails: ProductDetails? = null

        private val btnSubscribe: Button = view.findViewById(R.id.btn_subscribe)

        init {
            btnSubscribe.setOnClickListener {
                productDetails?.let {
                    onItemClickListener?.onSubscriptionClick(it)
                }
            }
        }

        fun bind(productDetails: ProductDetails) {
            this.productDetails = productDetails

            val price = productDetails.subscriptionOfferDetails?.first()?.pricingPhases?.pricingPhaseList?.first()?.formattedPrice
            btnSubscribe.text = itemView.context.getString(R.string.monthly_subscription_text, price)

        }
    }

    fun interface OnSubscriptionClickListener {
        fun onSubscriptionClick(product: ProductDetails)
    }
}