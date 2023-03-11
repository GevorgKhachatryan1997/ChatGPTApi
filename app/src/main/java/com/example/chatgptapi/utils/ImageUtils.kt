package com.example.chatgptapi.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.example.chatgptapi.ui.viewModel.ChatViewModel
import java.io.IOException

object ImageUtils {

    @Throws(IOException::class)
    fun saveImageToGallery(context: Context, bitmap: Bitmap) {
        val filename = "image_${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            uri?.let { imageUri ->
                context.contentResolver.openOutputStream(imageUri).use { outputStream ->
                    outputStream?.let {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                        Log.d(ChatViewModel::class.simpleName, "Image saved to gallery: $imageUri")
                    }
                }
            }
        } catch (e: IOException) {
            Log.e(ChatViewModel::class.simpleName, "Error saving image", e)
        }
    }
}