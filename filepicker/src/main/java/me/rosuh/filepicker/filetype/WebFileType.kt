package me.rosuh.filepicker.filetype

import me.rosuh.filepicker.R

/**
 *
 * Web 文件类型
 * @author rosu
 * @date 2018/11/27
 */
class WebFileType : FileType {
    override val fileType: String
        get() = "Web"
    override val fileIconResId: Int
        get() = R.drawable.ic_html_file_picker

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
            "asp", "aspx", "cer", "cfm", "csr", "css",
            "dcr", "html", "htm", "js", "jsp", "php",
            "rss", "xhtml" -> {
                true
            }
            else -> {
                false
            }
        }
    }
}