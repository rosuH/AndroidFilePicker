package me.rosuh.filepicker.config

import me.rosuh.filepicker.bean.FileItemBean

/**
 *
 * @author rosu
 * @date 2018/11/23
 */
interface FileFilter {

    /**
     * 自定义过滤器接口
     * @param 未处理的文件列表数据 ArrayList<FileItemBean>
     * @return ArrayList<FileItemBean>
     */
    fun selfFilter(listData: ArrayList<FileItemBean>): ArrayList<FileItemBean>
}