package me.rosuh.filepicker.bean

import me.rosuh.filepicker.bean.IFileBean

/**
 *
 * @author rosu
 * @date 2018/11/21
 */
class FileNavBean(val dirName:String, val dirPath:String): IFileBean {
    override var fileName: String
        get() = dirName
        set(value) {}
    override var filePath: String
        get() = dirPath
        set(value) {}
}