package me.rosuh.filepicker.bean

/**
 *
 * @author rosu
 * @date 2018/11/21
 *
 * 文件列表项
 * @property mFileName 文件名
 * @property mFilePath 文件路径
 * @property isChecked 是否被选中
 * @property fileType 文件类型
 * @property isHide 是否为隐藏文件，以符号 . 开头的视为隐藏文件
 *
 * IFileBean 接口属性
 * @property fileName  接口文件名
 * @property filePath 接口文件路径
 * @constructor
 */
class FileItemBean(var mFileName:String, var mFilePath:String, var isChecked:Boolean, var fileType: FileTypeEnum, var isHide:Boolean):
    IFileBean {

    override var fileName: String
        get() = mFileName
        set(value) {}
    override var filePath: String
        get() = mFilePath
        set(value) {}
}