package me.rosuh.filepicker.filetype

/**
 *
 * @author rosu
 * @date 2018/11/27
 */
interface IFileType {
    /**
     * 文件类型
     */
    val fileType:String

    val fileIconResId:Int
    /**
     * 传入文件路径，判断是否为该类型
     * @param filePath String
     * @return Boolean
     */
    fun verify(filePath:String):Boolean
}