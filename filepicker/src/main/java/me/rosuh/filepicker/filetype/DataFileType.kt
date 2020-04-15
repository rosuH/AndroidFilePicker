package me.rosuh.filepicker.filetype

import me.rosuh.filepicker.R

/**
 *
 * 数据文件类型
 * @author rosu
 * @date 2018/11/27
 */
class DataFileType : FileType {

    override val fileType: String
        get() = "DataFile"
    override val fileIconResId: Int
        get() = R.drawable.ic_unknown_file_picker

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
            "csv", "dat", "ged", "key", "keychain", "pps",
            "ppt", "pptx", "sdf", "tar", "tax2016", "tax2017",
            "vcf", "xml" -> {
                true
            }
            else -> {
                false
            }
        }
    }
}