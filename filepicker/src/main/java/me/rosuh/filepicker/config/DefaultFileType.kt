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
        fileTypes.add(AudioFileType("Audio", -1))
        fileTypes.add(RasterImageFileType("Image", R.drawable.ic_image))
        fileTypes.add(CompressedFileType("Compressed", R.drawable.ic_compressed))
        fileTypes.add(DataBaseFileType("DataBase", -1))
        fileTypes.add(ExecutableFileType("Executable", -1))
        fileTypes.add(FontFileType("Font", -1))
        fileTypes.add(PageLayoutFileType("PageLayout", -1))
        fileTypes.add(TextFileType("Text", -1))
        fileTypes.add(VideoFileType("Video", R.drawable.ic_video))
        fileTypes.add(WebFileType("Web", -1))
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