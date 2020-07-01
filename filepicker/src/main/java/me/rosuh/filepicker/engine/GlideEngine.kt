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
import me.rosuh.filepicker.R
import me.rosuh.filepicker.utils.ScreenUtils

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
            .addListener(object : RequestListener<Bitmap?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    imageView.setImageResource(R.drawable.ic_unknown_file_picker)
                    return true
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    // Create thumbnail for better effect.
                    val thumbnailBitmap =
                        ThumbnailUtils.extractThumbnail(
                            resource,
                            imageView.width.coerceAtLeast(ScreenUtils.dipToPx(context, 40f)),
                            imageView.height.coerceAtLeast(ScreenUtils.dipToPx(context, 40f))
                        )
                    imageView.setImageBitmap(thumbnailBitmap)
                    return true
                }
            })
            .into(imageView)
    }
}