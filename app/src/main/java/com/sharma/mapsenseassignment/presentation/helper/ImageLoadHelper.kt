package com.sharma.mapsenseassignment.presentation.helper

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun loadImage(url: String, context: Context): Bitmap? = withContext(Dispatchers.IO) {
    try {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .submit()
            .get()
    } catch (e: Exception) {
        null
    }
}
