package me.rosuh.filepicker.utils

import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.rosuh.filepicker.bean.BeanSubscriber
import me.rosuh.filepicker.bean.FileItemBeanImpl
import me.rosuh.filepicker.bean.FileNavBeanImpl
import me.rosuh.filepicker.config.FilePickerManager
import me.rosuh.filepicker.config.StorageMediaTypeEnum.EXTERNAL_STORAGE
import java.io.File
import java.util.*

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
        suspend fun getRootFile() = withContext(Dispatchers.IO){
            when (FilePickerManager.config.mediaStorageType) {
                EXTERNAL_STORAGE -> {
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
        suspend fun produceListDataSource(rootFile: File, beanSubscriber: BeanSubscriber) = withContext(Dispatchers.IO) {
            var listData: ArrayList<FileItemBeanImpl> = ArrayList()
            for (file in rootFile.listFiles()) {
                //以符号 . 开头的视为隐藏文件或隐藏文件夹，后面进行过滤
                val isHiddenFile = file.name.startsWith(".")
                if (file.isDirectory) {
                    listData.add(FileItemBeanImpl(file.name, file.path, false, null, true, isHiddenFile, beanSubscriber))
                    continue
                }
                val itemBean = FileItemBeanImpl(file.name, file.path, false, null, false, isHiddenFile, beanSubscriber)
                // 如果调用者没有实现文件类型甄别器，则使用的默认甄别器
                FilePickerManager.config.selfFileType?.fillFileType(itemBean) ?: FilePickerManager.config.defaultFileType.fillFileType(itemBean)
                listData.add(itemBean)
            }
            listData.run {
                // 隐藏文件处理
                this.hideFiles<FileItemBeanImpl>(!FilePickerManager.config.isShowHiddenFiles)
                // 排序
                this.sortWith(compareBy({!it.isDir}, {it.fileName}))
            }
            // 将当前列表数据暴露，以供调用者自己处理数据
            FilePickerManager.config.selfFilter?.doFilter(listData)?:listData
        }

        /**
         * 为导航栏添加数据，也就是每进入一个文件夹，导航栏的列表就添加一个对象
         * 如果是退回到上层文件夹，则删除后续子目录元素
         */
        fun produceNavDataSource(
            currentDataSource: ArrayList<FileNavBeanImpl>,
            nextPath: String
        ): ArrayList<FileNavBeanImpl> {

            if (currentDataSource.isEmpty()) {
                // 如果为空，为根目录
                currentDataSource.add(
                    FileNavBeanImpl(
                        FilePickerManager.config.mediaStorageName,
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
                    return ArrayList(currentDataSource.subList(0, currentDataSource.indexOf(data) + 1))
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
    }
}

private fun <E> java.util.ArrayList<FileItemBeanImpl>?.hideFiles(hide: Boolean) {
    if (hide) {
        this?.removeAll { it.isHide }
    }
}
