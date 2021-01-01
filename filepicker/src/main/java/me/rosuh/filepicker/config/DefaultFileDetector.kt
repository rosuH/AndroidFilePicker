package me.rosuh.filepicker.config

import me.rosuh.filepicker.bean.FileItemBeanImpl
import me.rosuh.filepicker.filetype.*

/**
 *
 * @author rosu
 * @date 2018/11/27
 */
class DefaultFileDetector : AbstractFileDetector() {

    var enableCustomTypes: Boolean = false
        private set

    private val allDefaultFileType: ArrayList<FileType> by lazy {
        ArrayList<FileType>()
    }

    fun registerDefaultTypes() {
        with(allDefaultFileType) {
            clear()
            add(AudioFileType())
            add(RasterImageFileType())
            add(CompressedFileType())
            add(DataBaseFileType())
            add(ExecutableFileType())
            add(FontFileType())
            add(PageLayoutFileType())
            add(TextFileType())
            add(VideoFileType())
            add(WebFileType())
        }
        enableCustomTypes = false
    }

    /**
     * @author rosuh@qq.com
     * @date 2020/9/16
     * save user's custom file types
     */
    fun registerCustomTypes(customFileTypes: ArrayList<FileType>) {
        allDefaultFileType.clear()
        allDefaultFileType.addAll(customFileTypes)
        enableCustomTypes = true
    }

    fun clear(){
        allDefaultFileType.clear()
        enableCustomTypes = false
    }

    override fun fillFileType(itemBeanImpl: FileItemBeanImpl): FileItemBeanImpl {
        for (type in allDefaultFileType) {
            if (type.verify(itemBeanImpl.fileName)) {
                itemBeanImpl.fileType = type
                break
            }
        }
        return itemBeanImpl
    }
}