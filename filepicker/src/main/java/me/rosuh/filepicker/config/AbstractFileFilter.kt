package me.rosuh.filepicker.config

import me.rosuh.filepicker.bean.FileItemBeanImpl

/**
 *
 * @author rosu
 * @date 2018/11/23
 */
abstract class AbstractFileFilter {
    /**
     * 自定义过滤器接口，此接口在生成列表数据的时候被调用
     * 返回一个经过处理的列表数据，进而生成列表视图
     * @param  listData ArrayList<FileItemBeanImpl> 未处理的文件列表数据
     * @return ArrayList<FileItemBeanImpl>
     */
    abstract fun doFilter(listData: ArrayList<FileItemBeanImpl>): ArrayList<FileItemBeanImpl>
}