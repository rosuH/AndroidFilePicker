package me.rosuh.filepicker.filetype

/**
 *
 * 音频文件类型
 * @author rosu
 * @date 2018/11/27
 */
class AudioFileType(override val fileType: String, override val fileIconResId: Int): IFileType {

    override fun verify(filePath: String): Boolean {
        /**
         * 使用 endWith 是不可靠的，因为文件名有可能是以格式结尾，但是没有 . 符号
         * 比如 文件名仅为：example_png
         */
        val isHasSuffix = filePath.contains(".")
        if (!isHasSuffix){
            // 如果没有 . 符号，即是没有文件后缀
            return false
        }
        val suffix = filePath.substring(filePath.lastIndexOf("."))
        return when (suffix){
            "aif", "iff", "m3u", "m4a", "mid", "mp3", "mpa", "wav", "wma" -> {
                true
            }
            else -> {
                false
            }
        }
    }
}