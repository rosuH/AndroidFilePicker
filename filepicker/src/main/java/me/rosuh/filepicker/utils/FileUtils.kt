package me.rosuh.filepicker.utils

import android.os.Environment
import me.rosuh.filepicker.bean.FileItemBean
import me.rosuh.filepicker.bean.FileNavBean
import me.rosuh.filepicker.bean.FileTypeEnum
import me.rosuh.filepicker.bean.FileTypeEnum.DIR
import me.rosuh.filepicker.config.FilePickerManager
import me.rosuh.filepicker.config.StorageMediaTypeEnum.EXTERNAL_STORAGE
import java.io.File

/**
 *
 * @author rosu
 * @date 2018/11/22
 */
class FileUtils {

    companion object {
        /**
         * 根据配置参数获取根目录文件
         * @return File
         */
        fun getRootFile():File{
            when(FilePickerManager.mediaStorageType){
                EXTERNAL_STORAGE -> {
                    return File(Environment.getExternalStorageDirectory().absoluteFile.toURI())
                }
                else -> {
                    return File(Environment.getExternalStorageDirectory().absoluteFile.toURI())
                }
            }
        }

        /**
         * 根据传入的文件名返回文件类型
         * 判断的方式是根据文件的后缀名
         * @param fileName 文件名
         * @return 文件类型
         */
        fun getFileType(fileName: String): FileTypeEnum {
            val index = fileName.lastIndexOf(".")
            if (index == -1) return FileTypeEnum.UNKNOWN

            when (fileName.substring(index + 1)) {
                "png", "jpg", "jpeg" -> {
                    return FileTypeEnum.IMAGE
                }
                "zip", "tar", "rar", "tgz", "7zip" -> {
                    return FileTypeEnum.COMPRESSED
                }
                "mp4", "mkv", "mov" -> {
                    return FileTypeEnum.VIDEO
                }
                else -> {
                    return FileTypeEnum.UNKNOWN
                }
            }
        }

        /**
         * 获取给定文件对象下的所有文件，生成列表项对象
         * @param rootFile File
         * @return ArrayList<FileItemBean>
         */
        fun produceListDataSource(rootFile: File): ArrayList<FileItemBean> {
            var listData = ArrayList<FileItemBean>()
            for (file in rootFile.listFiles()) {
                //以符号 . 开头的视为隐藏文件或隐藏文件夹，后面进行过滤
                val isHidedFile = file.name.startsWith(".")
                if (file.isDirectory) {
                    listData.add(FileItemBean(file.name, file.path, false, DIR, isHidedFile))
                    continue
                }
                listData.add(
                    FileItemBean(
                        file.name,
                        file.path,
                        false,
                        getFileType(file.name),
                        isHidedFile
                    )
                )
            }
            // 隐藏文件过滤器
            if (!FilePickerManager.isShowHidingFiles) {
                listData = filesHiderFilter(listData)
            }

            if (FilePickerManager.selfFilter != null){
                listData = FilePickerManager.selfFilter!!.selfFilter(listData)
            }

            return listData
        }

        /**
         * 隐藏文件的过滤器，传入列表的数据集，然后将被视为隐藏文件的条目从中删除
         * @param listData ArrayList<FileItemBean>
         */
        fun filesHiderFilter(listData: ArrayList<FileItemBean>): ArrayList<FileItemBean> {
            return ArrayList(listData.filter { !it.isHide })
        }

        /**
         * 为导航栏添加数据，也就是每进入一个文件夹，导航栏的列表就添加一个对象
         * 如果是退回到上层文件夹，则删除后续子目录元素
         */
        fun produceNavDataSource(
            currentDataSource: ArrayList<FileNavBean>,
            nextPath: String
        ): ArrayList<FileNavBean> {

            if (currentDataSource.isEmpty()) {
                // 如果为空，为根目录
                currentDataSource.add(
                    FileNavBean(
                        FilePickerManager.mediaStorageName,
                        nextPath
                    )
                )
                return currentDataSource
            }

            for (data in currentDataSource) {
                // 如果是回到根目录
                if (nextPath.equals(currentDataSource.first().dirPath)) {
                    return ArrayList(currentDataSource.subList(0, 1))
                }
                // 如果是回到当前目录（不包含根目录情况）
                // 直接返回
                val isCurrent = nextPath.equals(currentDataSource.get(currentDataSource.size - 1).dirPath)
                if (isCurrent) {
                    return currentDataSource
                }

                // 如果是回到上层的某一目录(即，当前列表中有该路径)
                // 将列表截取至目标路径元素
                val isBackToAbove = nextPath.equals(data.dirPath)
                if (isBackToAbove) {
                    return ArrayList(currentDataSource.subList(0, currentDataSource.indexOf(data) + 1))
                }
            }
            // 循环到此，意味着将是进入子目录
            currentDataSource.add(
                FileNavBean(
                    nextPath.substring(nextPath.lastIndexOf("/") + 1),
                    nextPath
                )
            )
            return currentDataSource
        }
    }
}