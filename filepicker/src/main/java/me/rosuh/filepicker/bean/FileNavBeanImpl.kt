package me.rosuh.filepicker.bean

/**
 *
 * @author rosu
 * @date 2018/11/21
 */
class FileNavBeanImpl(val dirName: String, val dirPath: String) : FileBean {
    override var fileName: String
        get() = dirName
        set(value) {}
    override var filePath: String
        get() = dirPath
        set(value) {}
}