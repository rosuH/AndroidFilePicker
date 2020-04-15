package me.rosuh.filepicker.filetype

import me.rosuh.filepicker.R

/**
 *
 * 视频文件类型
 * @author rosu
 * @date 2018/11/27
 */
class VideoFileType : FileType {

    override val fileType: String
        get() = "Video"
    override val fileIconResId: Int
        get() = R.drawable.ic_video_file_picker

    override fun verify(fileName: String): Boolean {
        /**
         * 使用 endWith 是不可靠的，因为文件名有可能是以格式结尾，但是没有 . 符号
         * 比如 文件名仅为：example_png
         */
        val isHasSuffix = fileName.contains(".")
        if (!isHasSuffix) {
            // 如果没有 . 符号，即是没有文件后缀
            return false
        }
        val suffix = fileName.substring(fileName.lastIndexOf(".") + 1)
        return when (suffix) {
            "mp4", "mkv", "mov", "mpg", "mpeg", "3gp",
            "3gpp", "3g2", "3gpp2", "webm", "ts", "avi",
            "flv", "swf", "wmv", "vob", "m4v" -> {
                true
            }
            else -> {
                false
            }
        }
    }
}