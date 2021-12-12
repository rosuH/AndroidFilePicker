package me.rosuh.filepicker.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.rosuh.filepicker.FilePickerActivity
import me.rosuh.filepicker.R
import me.rosuh.filepicker.bean.FileNavBeanImpl

/**
 *
 * @author rosu
 * @date 2018/11/21
 */
class FileNavAdapter(
    private val activity: FilePickerActivity
) : BaseAdapter() {
    private lateinit var recyclerView: RecyclerView
    val dataList: ArrayList<FileNavBeanImpl> = ArrayList(3)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (parent is RecyclerView) {
            recyclerView = parent
        }
        return NavListHolder(activity.layoutInflater, parent)
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

    override fun getItem(position: Int): FileNavBeanImpl? {
        return if (position >= 0 && position < dataList.size) {
            dataList[position]
        } else {
            null
        }
    }

    fun setNewData(list: List<FileNavBeanImpl>?) {
        list?.let {
            dataList.clear()
            dataList.addAll(it)
            notifyDataSetChanged()
        }
    }

    inner class NavListHolder(inflater: LayoutInflater, val parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_nav_file_picker, parent, false)) {

        private var mBtnDir: TextView? = null

        private var pos: Int? = null

        fun bind(item: FileNavBeanImpl?, position: Int) {
            pos = position
            mBtnDir = itemView.findViewById(R.id.tv_btn_nav_file_picker)
            mBtnDir?.text = item!!.dirName
        }
    }
}