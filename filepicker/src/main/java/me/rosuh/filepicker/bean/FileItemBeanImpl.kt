package me.rosuh.filepicker.bean

import me.rosuh.filepicker.filetype.FileType
import java.io.File

/**
 *
 * @author rosu
 * @date 2018/11/21
 *
 * 文件列表项
 * @property isChecked 是否被选中
 * @property fileType 文件类型
 * @property isHide 是否为隐藏文件，以符号 . 开头的视为隐藏文件
 *
 * FileBean 接口属性
 * @property fileName  接口文件名
 * @property filePath 接口文件路径
 * @constructor
 */
class FileItemBeanImpl(
    override var fileName: String,
    override var filePath: String,
    private var isChecked: Boolean,
    var fileType: FileType?,
    val isDir: Boolean,
    var isHide: Boolean,
) : FileBean {

    fun isChecked(): Boolean {
        return isChecked
    }

    fun setCheck(check: Boolean) {
        isChecked = check
    }
}