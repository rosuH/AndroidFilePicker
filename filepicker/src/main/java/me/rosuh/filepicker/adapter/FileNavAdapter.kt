package me.rosuh.filepicker.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.rosuh.filepicker.R
import me.rosuh.filepicker.bean.FileItemBeanImpl
import me.rosuh.filepicker.bean.FileNavBeanImpl

/**
 *
 * @author rosu
 * @date 2018/11/21
 */
class FileNavAdapter : BaseAdapter() {
    private lateinit var recyclerView: RecyclerView
    val dataList: ArrayList<FileItemBeanImpl> = ArrayList(3)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (parent is RecyclerView) {
            recyclerView = parent
        }
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_nav_file_picker, parent, false)
        return NavListHolder(itemView)
    }

    override fun getItemView(position: Int): View? {
        return recyclerView.findViewHolderForAdapterPosition(position)?.itemView
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, postion: Int) {
        (holder as NavListHolder).bind(dataList[postion], postion)
    }

    override fun getItem(position: Int): FileItemBeanImpl? {
        return dataList.getOrNull(position)
    }

    fun setNewData(list: List<FileItemBeanImpl>?) {
        list?.let {
            dataList.clear()
            dataList.addAll(it)
            notifyDataSetChanged()
        }
    }

    inner class NavListHolder(itemView: View) : FileListAdapter.BaseViewHolder(itemView) {

        private var mBtnDir: TextView? = null

        private var pos: Int? = null

        override fun bind(itemImpl: FileItemBeanImpl, position: Int) {
            pos = position
            mBtnDir = itemView.findViewById(R.id.tv_btn_nav_file_picker)
            mBtnDir?.apply {
                text = itemImpl.fileName
            }
            itemView.apply {
                setOnClickListener {
                    this@FileNavAdapter.clickListener?.onItemClick(this@FileNavAdapter, it, position)
                }
            }
        }
    }
}