package me.rosuh.filepicker.config

import me.rosuh.filepicker.R
import me.rosuh.filepicker.bean.FileItemBean
import me.rosuh.filepicker.filetype.*

/**
 *
 * @author rosu
 * @date 2018/11/27
 */
class DefaultFileType: AbstractFileType(){

    private val allDefaultFileType:ArrayList<IFileType> by lazy {
        val fileTypes = ArrayList<IFileType>()
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

    override fun fillFileType(itemBean: FileItemBean): FileItemBean {
        for (type in allDefaultFileType){
            if (type.verify(itemBean.fileName)){
                itemBean.fileType = type
                break
            }
        }
        return itemBean
    }
}