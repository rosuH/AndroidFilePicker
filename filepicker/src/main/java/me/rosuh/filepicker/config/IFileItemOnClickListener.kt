package me.rosuh.filepicker.config

import me.rosuh.filepicker.bean.FileItemBean

/**
 *
 * @author rosu
 * @date 2018/11/26
 */
interface IFileItemOnClickListener {
    /**
     * 条目被点击接口
     * @param item 被点击的条目的对象
     * @param pos 被点击的条目在列表中的位置
     */
    fun onItemClick(item:FileItemBean, pos:Int)

    fun onItemChildClick()
}