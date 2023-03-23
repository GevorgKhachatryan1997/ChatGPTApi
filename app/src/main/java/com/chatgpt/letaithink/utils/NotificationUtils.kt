package com.chatgpt.letaithink.utils

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.provider.Settings
import com.chatgpt.letaithink.MainActivity
import com.chatgpt.letaithink.R

@SuppressLint("StaticFieldLeak")
object NotificationUtils {

    private lateinit var context: Context

    private const val BUBBLE_VIEW_CHANNEL_ID = "bubble_channel_id"
    private const val BUBBLE_VIEW_CHANNEL_NAME = "Bubble view"

    private val notificationManager: NotificationManager by lazy { context.getSystemService(NotificationManager::class.java) }

    fun init(context: Context) {
        this.context = context

        createBubbleViewChanel()
    }

    fun showBubbleViewNotification() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return
        }

        val message = context.getString(R.string.bubble_notification_message)
        val description = context.getString(R.string.bubble_notification_settings_description)

        val chatPartner = Person.Builder()
            .setName(message)
            .setImportant(true)
            .build()
        ShortCutUtils.createBubbleViewShortCut(chatPartner)

        val mainActivity = Intent(context, MainActivity::class.java)

        val bubbleIntent =
            PendingIntent.getActivity(context, 0, mainActivity, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        val bubbleData = Notification.BubbleMetadata.Builder(
            bubbleIntent,
            Icon.createWithResource(context, R.mipmap.ic_launcher)
        )
            .setAutoExpandBubble(true)
            .setSuppressNotification(true)
            .build()

        val builder = Notification.Builder(context, BUBBLE_VIEW_CHANNEL_ID)
            .setContentIntent(bubbleIntent)
            .setLargeIcon(Icon.createWithResource(context, R.mipmap.ic_launcher))
            .setSmallIcon(R.mipmap.ic_launcher)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    setBubbleMetadata(bubbleData)
                }
                style = Notification.MessagingStyle(chatPartner)
                    .setGroupConversation(false)
                    .addMessage(description, 0, chatPartner)
            }
            .setShortcutId(ShortCutUtils.BUBBLE_VIEW_SHORTCUT_ID)
            .setCategory(Notification.CATEGORY_MESSAGE)
        notificationManager.notify(1, builder.build())
    }

    fun notificationSettingsIntent() = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    private fun createBubbleViewChanel() {
        val channelImportance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(BUBBLE_VIEW_CHANNEL_ID, BUBBLE_VIEW_CHANNEL_NAME, channelImportance)

        notificationManager.createNotificationChannel(channel)
    }
}