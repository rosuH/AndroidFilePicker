package me.rosuh.filepicker.engine

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import me.rosuh.filepicker.R

/**
 * @author rosu
 * @date 2020-04-15
 * 一个全局的图片加载控制类，包含了判断是否存在以及存在哪种图片加载引擎。
 * A global image loading control class, including determining whether there is and what kind of
 * image loading engine exists.
 */
object ImageLoadController {
    private val enableGlide by lazy {
        try {
            Class.forName("com.bumptech.glide.Glide")
            true
        } catch (e: ClassNotFoundException) {
            false
        } catch (e: ExceptionInInitializerError) {
            false
        }
    }

    private val enablePicasso by lazy {
        try {
            Class.forName("com.squareup.picasso.Picasso")
            true
        } catch (e: ClassNotFoundException) {
            false
        } catch (e: ExceptionInInitializerError) {
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

    /**
     * 加载图片，如果没有不存在图片加载引擎，那么家使用默认 icon
     * Load images, if there is no image loading engine, then the default icon is used
     */
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