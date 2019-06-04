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
         * 获取给定文件对象 @param rootFil 下的所有文件，生成列表项对象 @return ArrayList<FileItemBeanImpl>
         */
        suspend fun produceListDataSource(rootFile: File, beanSubscriber: BeanSubscriber) = withContext(Dispatchers.IO) {
            var listData: ArrayList<FileItemBeanImpl>? = ArrayList()
            for (file in rootFile.listFiles()) {
                //以符号 . 开头的视为隐藏文件或隐藏文件夹，后面进行过滤
                val isHiddenFile = file.name.startsWith(".")
                if (file.isDirectory) {
                    listData?.add(FileItemBeanImpl(file.name, file.path, false, null, true, isHiddenFile, beanSubscriber))
                    continue
                }
                val itemBean = FileItemBeanImpl(file.name, file.path, false, null, false, isHiddenFile, beanSubscriber)
                // 如果调用者没有实现文件类型甄别器，则使用的默认甄别器
                FilePickerManager.config.selfFileType?.fillFileType(itemBean) ?: FilePickerManager.config.defaultFileType.fillFileType(itemBean)
                listData?.add(itemBean)
            }
            listData.apply {
                // 隐藏文件过滤器
                filesHiderFilter(this!!)
                // 将当前列表数据暴露，以供调用者自己处理数据
                FilePickerManager.config.selfFilter?.doFilter(this)
                // 排序
                sortWith(compareBy({!it.isDir}, {it.fileName}))
            }
        }

        /**
         * 隐藏文件的过滤器，传入列表的数据集[listData]，然后将被视为隐藏文件的条目从中删除
         */
        private fun filesHiderFilter(listData: ArrayList<FileItemBeanImpl>){
            listData.retainAll { listData.filter { !it.isHide } }
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