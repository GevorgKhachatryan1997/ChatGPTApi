package com.chatgpt.letaithink.utils

import android.annotation.SuppressLint
import android.app.Person
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.os.Build
import com.chatgpt.letaithink.R

@SuppressLint("StaticFieldLeak")
object ShortCutUtils {

    const val BUBBLE_VIEW_SHORTCUT_ID = "bubble_view_shortcat_id"

    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
    }

    fun createBubbleViewShortCut(chatPartner: Person) {
        val title = context.getString(R.string.enable_bubble_chat)
        val shortcut =
            ShortcutInfo.Builder(context, BUBBLE_VIEW_SHORTCUT_ID)
                .setIntent(Intent(Intent.ACTION_DEFAULT))
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        setLongLived(true)
                        setPerson(chatPartner)
                    }
                }
                .setShortLabel(title)
                .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val shortcutManager = context.getSystemService(ShortcutManager::class.java)
            shortcutManager.pushDynamicShortcut(shortcut)
        }
    }
}