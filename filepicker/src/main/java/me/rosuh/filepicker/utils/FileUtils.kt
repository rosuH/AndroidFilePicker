package me.rosuh.filepicker.utils

import android.content.Context
import android.os.Environment
import me.rosuh.filepicker.bean.FileItemBeanImpl
import me.rosuh.filepicker.bean.FileNavBeanImpl
import me.rosuh.filepicker.config.FilePickerConfig.Companion.STORAGE_CUSTOM_ROOT_PATH
import me.rosuh.filepicker.config.FilePickerConfig.Companion.STORAGE_EXTERNAL_STORAGE
import me.rosuh.filepicker.config.FilePickerManager.config
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

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
        fun getRootFile(): File {
            return when (config.mediaStorageType) {
                STORAGE_EXTERNAL_STORAGE -> {
                    File(Environment.getExternalStorageDirectory().absoluteFile.toURI())
                }
                else -> {
                    File(Environment.getExternalStorageDirectory().absoluteFile.toURI())
                }
            }
        }

        /**
         * 获取给定文件对象[rootFile]下的所有文件，生成列表项对象
         */
        fun produceListDataSource(
            rootFile: File
        ): ArrayList<FileItemBeanImpl> {
            val listData: ArrayList<FileItemBeanImpl> = ArrayList()
            var isDetected = false
            // 查看是否在根目录上级
            val realRoot = getRootFile()
            val isInRootParent = rootFile.list() == null
                    && !config.isSkipDir
                    && rootFile.path == realRoot.parentFile?.path
            if (isInRootParent) {
                // 如果是文件夹作为可选项时，需要让根目录也作为 item 被点击
                listData.add(
                    FileItemBeanImpl(
                        realRoot.name,
                        realRoot.path,
                        false,
                        null,
                        isDir = true,
                        isHide = false
                    )
                )
                return config.selfFilter?.doFilter(listData) ?: listData
            }
            val listFiles = rootFile.listFiles()
            if (listFiles.isNullOrEmpty()) {
                return listData
            }
            for (file in listFiles) {
                //以符号 . 开头的视为隐藏文件或隐藏文件夹，后面进行过滤
                val isHiddenFile = file.name.startsWith(".")
                if (!config.isShowHiddenFiles && isHiddenFile) {
                    // skip hidden files
                    continue
                }
                if (file.isDirectory) {
                    listData.add(
                        FileItemBeanImpl(
                            file.name,
                            file.path,
                            false,
                            null,
                            true,
                            isHiddenFile
                        )
                    )
                    continue
                }
                val itemBean = FileItemBeanImpl(
                    file.name,
                    file.path,
                    false,
                    null,
                    false,
                    isHiddenFile
                )
                // 如果调用者没有实现文件类型甄别器，则使用的默认甄别器
                config.customDetector?.fillFileType(itemBean)
                    ?: config.defaultFileDetector.fillFileType(itemBean)
                isDetected = itemBean.fileType != null
                if (config.defaultFileDetector.enableCustomTypes
                    && config.isAutoFilter
                    && !isDetected
                ) {
                    // enable auto filter AND using user's custom file type. Filter them.
                    continue
                }
                listData.add(itemBean)
            }

            // 默认字典排序
            // Default sort by alphabet
            listData.sortWith(
                compareBy(
                    { !it.isDir },
                    { it.fileName.uppercase(Locale.getDefault()) })
            )
            // 将当前列表数据暴露，以供调用者自己处理数据
            // expose data list  to outside caller
            return config.selfFilter?.doFilter(listData) ?: listData
        }

        /**
         * 为导航栏添加数据，也就是每进入一个文件夹，导航栏的列表就添加一个对象
         * 如果是退回到上层文件夹，则删除后续子目录元素
         */
        fun produceNavDataSource(
            currentDataSource: ArrayList<FileNavBeanImpl>,
            nextPath: String,
            context: Context
        ): ArrayList<FileNavBeanImpl> {
            // 优先级：目标设备名称 --> 自定义路径 --> 默认 SD 卡
            if (currentDataSource.isEmpty()) {
                val dirName = getDirAlias(getRootFile())
                currentDataSource.add(
                    FileNavBeanImpl(
                        dirName,
                        nextPath
                    )
                )
                return currentDataSource
            }

            for (data in currentDataSource) {
                // 如果是回到根目录
                if (nextPath == currentDataSource.first().dirPath) {
                    return ArrayList(currentDataSource.subList(0, 1))
                }
                // 如果是回到当前目录（不包含根目录情况）
                // 直接返回
                val isCurrent = nextPath == currentDataSource[currentDataSource.size - 1].dirPath
                if (isCurrent) {
                    return currentDataSource
                }

                // 如果是回到上层的某一目录(即，当前列表中有该路径)
                // 将列表截取至目标路径元素
                val isBackToAbove = nextPath == data.dirPath
                if (isBackToAbove) {
                    return ArrayList(
                        currentDataSource.subList(
                            0,
                            currentDataSource.indexOf(data) + 1
                        )
                    )
                }
            }
            // 循环到此，意味着将是进入子目录
            currentDataSource.add(
                FileNavBeanImpl(
                    nextPath.substring(nextPath.lastIndexOf("/") + 1),
                    nextPath
                )
            )
            return currentDataSource
        }

        fun getDirAlias(file: File): String {
            val isCustomRoot = config.mediaStorageType == STORAGE_CUSTOM_ROOT_PATH
                    && file.absolutePath == config.customRootPath
            val isPreSetStorageRoot = config.mediaStorageType == STORAGE_EXTERNAL_STORAGE
                    && file.absolutePath == getRootFile().absolutePath
            val isDefaultRoot = file.absolutePath == getRootFile().absolutePath
            return when {
                isCustomRoot || isPreSetStorageRoot -> {
                    config.mediaStorageName
                }
                isDefaultRoot -> {
                    config.defaultStorageName
                }
                else -> {
                    file.name
                }
            }
        }
    }
}