package me.rosuh.filepicker.engine

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

/**
 * @author rosu
 * @date 2020-04-15
 * An [ImageEngine] implementation by using Glide
 */
class GlideEngine : ImageEngine {
    override fun loadImage(context: Context?, imageView: ImageView?, uri: Uri?, placeholder: Int) {
        if (context == null || imageView == null) {
            return
        }
        Glide.with(context)
            .load(uri)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    imageView.setImageResource(placeholder)
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

            })
            .into(imageView)
    }
}