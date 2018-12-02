package me.rosuh.filepicker.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import me.rosuh.filepicker.FilePickerActivity
import me.rosuh.filepicker.R
import me.rosuh.filepicker.bean.FileItemBeanImpl
import me.rosuh.filepicker.config.FilePickerConfig
import me.rosuh.filepicker.config.FilePickerManager
import java.io.File

/**
 *
 * @author rosu
 * @date 2018/11/21
 */
class FileListAdapter(private val activity: FilePickerActivity, var data: ArrayList<FileItemBeanImpl>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var recyclerViewListener: RecyclerViewListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(activity).inflate(R.layout.item_list_file_picker, parent, false)
        return FileListItemHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FileListItemHolder).bind(data[position], position)
    }

    fun getItem(position: Int):FileItemBeanImpl?{
        if (position >= 0 && position < data.size) return data[position]
        return null
    }

    inner class FileListItemHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener{

        private val isSkipDir: Boolean = FilePickerConfig.getInstance(FilePickerManager.instance).isSkipDir
        private val mTvFileName = itemView.findViewById<TextView>(R.id.tv_list_file_picker)!!
        val mCbItem = itemView.findViewById<CheckBox>(R.id.cb_list_file_picker)!!
        private val mIcon = itemView.findViewById<ImageView>(R.id.iv_icon_list_file_picker)!!
        private var mItemBeanImpl:FileItemBeanImpl ?= null
        private var mPosition:Int? = null


        fun bind(itemImpl: FileItemBeanImpl, position: Int) {
            mItemBeanImpl = itemImpl
            mPosition = position

            mTvFileName.text = itemImpl.fileName
            mCbItem.isChecked = itemImpl.isChecked
            mCbItem.visibility = View.VISIBLE

            val isDir = File(itemImpl.filePath).isDirectory

            if (isDir) {
                mIcon.setImageResource(R.drawable.ic_folder)
                mCbItem.visibility = if (isSkipDir) View.INVISIBLE else View.VISIBLE
                return
            }

            val resId: Int = itemImpl.fileType?.fileIconResId ?: R.drawable.ic_unknown
            mIcon.setImageResource(resId)

            mCbItem.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            recyclerViewListener?.itemClickListener?.onItemChildClick(
                this@FileListAdapter,
                mCbItem,
                mPosition!!
            )
        }
    }
}