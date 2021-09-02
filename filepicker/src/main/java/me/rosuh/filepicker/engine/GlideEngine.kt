package me.rosuh.filepicker.engine

import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeTransition
import me.rosuh.filepicker.R
import me.rosuh.filepicker.utils.dp

/**
 * @author rosu
 * @date 2020-04-15
 * An [ImageEngine] implementation by using Glide
 */
class GlideEngine : ImageEngine {
    override fun loadImage(
        context: Context?,
        imageView: ImageView?,
        url: String?,
        placeholder: Int
    ) {
        if (context == null || imageView == null) {
            return
        }
        Glide.with(context)
            .asBitmap()
            .load(url)
            .override(imageView.width.coerceAtLeast(40.dp))
            .error(R.drawable.ic_unknown_file_picker)
            .into(imageView)
    }
}