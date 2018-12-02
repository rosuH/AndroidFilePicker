package me.rosuh.filepicker.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import me.rosuh.filepicker.FilePickerActivity
import me.rosuh.filepicker.bean.FileNavBeanImpl
import me.rosuh.filepicker.R

/**
 *
 * @author rosu
 * @date 2018/11/21
 */
class FileNavAdapter(private val activity: FilePickerActivity, val data: MutableList<FileNavBeanImpl>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

     var recyclerViewListener: RecyclerViewListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NavListHolder(activity.layoutInflater, parent)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, postion: Int) {
        (holder as NavListHolder).bind(data[postion], postion)
    }

    fun getItem(position: Int): FileNavBeanImpl?{
        if (position >= 0 && position < data.size) return data[position]
        return null
    }

    inner class NavListHolder(inflater: LayoutInflater, val parent: ViewGroup):
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_nav_file_picker, parent, false)), View.OnClickListener{

        private lateinit var mBtnDir:Button

        private var pos:Int? = null

        fun bind(item: FileNavBeanImpl?, position:Int) {
            pos = position
            mBtnDir = itemView.findViewById(R.id.btn_nav_file_picker)
            mBtnDir.text = item!!.dirName
            mBtnDir.setOnClickListener(this@NavListHolder)
        }

        override fun onClick(v: View?) {
            recyclerViewListener?.itemClickListener?.onItemChildClick(
                this@FileNavAdapter,
                mBtnDir,
                pos!!
            )
        }
    }
}