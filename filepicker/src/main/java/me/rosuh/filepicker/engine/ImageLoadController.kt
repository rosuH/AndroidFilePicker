package me.rosuh.filepicker.engine

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import me.rosuh.filepicker.R

object ImageLoadController {
    private val enableGlide by lazy {
        try {
            Class.forName("com.bumptech.glide.Glide")
            true
        } catch (e: Exception) {
            false
        }
    }

    private val enablePicasso by lazy {
        try {
            Class.forName("com.squareup.picasso.Picasso")
            true
        } catch (e: Exception) {
            false
        }
    }

    private var engine: ImageEngine? = null

    init {
        engine = when {
            enableGlide -> {
                GlideEngine()
            }
            enablePicasso -> {
                PicassoEngine()
            }
            else -> {
                null
            }
        }
    }

    fun load(
        context: Context,
        iv: ImageView,
        uri: Uri,
        placeholder: Int? = R.drawable.ic_unknown_file_picker
    ) {
        if (engine == null) {
            iv.setImageResource(placeholder ?: R.drawable.ic_unknown_file_picker)
            return
        }
        engine?.loadImage(context, iv, uri, placeholder ?: R.drawable.ic_unknown_file_picker)
    }
}