package me.rosuh.filepicker.engine

import android.content.Context
import android.net.Uri
import android.widget.ImageView

interface ImageEngine {
    fun loadImage(
        context: Context?,
        imageView: ImageView?,
        uri: Uri?,
        placeholder: Int
    )
}