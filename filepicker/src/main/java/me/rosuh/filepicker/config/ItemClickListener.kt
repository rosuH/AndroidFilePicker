package me.rosuh.filepicker.config

import android.view.View
import me.rosuh.filepicker.adapter.FileListAdapter
/**
 * If the user return true means the event has been consumed.
 * @author rosuh@qq.com
 * @date 2021/8/23
*/
interface ItemClickListener {

    /**
     * Item root view click
     * @return true if the event has been consumed
     */
    fun onItemClick(
        itemAdapter: FileListAdapter,
        itemView: View,
        position: Int
    ): Boolean

    /**
     * Item child view click
     * @return true if the event has been consumed
     */
    fun onItemChildClick(
        itemAdapter: FileListAdapter,
        itemView: View,
        position: Int
    ): Boolean

    /**
     * Item root view long click
     * @return true if the event has been consumed
     */
    fun onItemLongClick(
        itemAdapter: FileListAdapter,
        itemView: View,
        position: Int
    ): Boolean
}