package me.rosuh.filepicker.config

import me.rosuh.filepicker.bean.FileItemBeanImpl
import me.rosuh.filepicker.filetype.*

/**
 *
 * @author rosu
 * @date 2018/11/27
 */
class DefaultFileType: AbstractFileType(){

    private val allDefaultFileType:ArrayList<FileType> by lazy {
        val fileTypes = ArrayList<FileType>()
        fileTypes.add(AudioFileType())
        fileTypes.add(RasterImageFileType())
        fileTypes.add(CompressedFileType())
        fileTypes.add(DataBaseFileType())
        fileTypes.add(ExecutableFileType())
        fileTypes.add(FontFileType())
        fileTypes.add(PageLayoutFileType())
        fileTypes.add(TextFileType())
        fileTypes.add(VideoFileType())
        fileTypes.add(WebFileType())
        fileTypes
    }

    override fun fillFileType(itemBeanImpl: FileItemBeanImpl): FileItemBeanImpl {
        for (type in allDefaultFileType){
            if (type.verify(itemBeanImpl.fileName)){
                itemBeanImpl.fileType = type
                break
            }
        }
        return itemBeanImpl
    }
}