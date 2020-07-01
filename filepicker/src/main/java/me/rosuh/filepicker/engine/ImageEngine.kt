package me.rosuh.filepicker.engine

import android.content.Context
import android.net.Uri
import android.widget.ImageView

/**
 * @author rosu
 * @date 2020-04-15
 * 描述图片加载器的接口，以便 Glide、Picasso 或其他加载器使用
 * Describe the interface of the picture loader for use by Glide, Picasso or other loaders
 */
interface ImageEngine {
    /**
     * 调用此接口加载图片，一般情况下[url]参数表示图片的本地路径 path，通过[Uri.parse]得到的值。通常是 file:/// 开头
     * 如果加载失败，将使用[placeholder]
     * Call this interface to load the picture. Generally, the [url] parameter indicates the local
     * path of the picture, and the value obtained through [Uri.parse].
     * Usually starts with file:///
     * If loading fails, [placeholder] will be used
     */
    fun loadImage(
        context: Context?,
        imageView: ImageView?,
        url: String?,
        placeholder: Int
    )
}