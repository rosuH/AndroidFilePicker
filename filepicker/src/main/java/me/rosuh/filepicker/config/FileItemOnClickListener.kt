package me.rosuh.filepicker.config

import android.support.v7.widget.RecyclerView
import android.view.View
import me.rosuh.filepicker.bean.FileItemBeanImpl

/**
 *
 * @author rosu
 * @date 2018/11/26
 */
interface FileItemOnClickListener {

    fun onItemClick(itemAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
                    itemView: View,
                    position: Int)

    fun onItemChildClick(itemAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
                         itemView: View,
                         position: Int)

    fun onItemLongClick(itemAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
                        itemView: View,
                        position: Int)
}