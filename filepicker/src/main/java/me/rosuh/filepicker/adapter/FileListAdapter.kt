package me.rosuh.filepicker.adapter

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
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
import me.rosuh.filepicker.config.FilePickerManager
import java.io.File

/**
 *
 * @author rosu
 * @date 2018/11/21
 * 文件列表适配器类
 */
class FileListAdapter(private val activity: FilePickerActivity, var data: ArrayList<FileItemBeanImpl>?) :
    BaseAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(activity).inflate(R.layout.item_list_file_picker, parent, false)
        return FileListItemHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data?.size ?: 10
    }

    override fun getItemViewType(position: Int): Int {
        return DEFAULT_FILE_TYPE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FileListItemHolder).bind(data!![position], position)
    }

    override fun getItem(position: Int): FileItemBeanImpl? {
        if (position >= 0 &&
            position < data!!.size &&
            getItemViewType(position) == DEFAULT_FILE_TYPE
        ) return data!![position]
        return null
    }

    inner class FileListItemHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val isSkipDir: Boolean = FilePickerManager.config.isSkipDir
        private val mTvFileName = itemView.findViewById<TextView>(R.id.tv_list_file_picker)!!
        private val mCbItem = itemView.findViewById<CheckBox>(R.id.cb_list_file_picker)!!
        private val mIcon = itemView.findViewById<ImageView>(R.id.iv_icon_list_file_picker)!!
        private var mItemBeanImpl: FileItemBeanImpl? = null
        private var mPosition: Int? = null


        fun bind(itemImpl: FileItemBeanImpl, position: Int) {
            mItemBeanImpl = itemImpl
            mPosition = position

            mTvFileName.text = itemImpl.fileName
            mCbItem.isChecked = itemImpl.isChecked()
            mCbItem.visibility = View.VISIBLE

            val isDir = File(itemImpl.filePath).isDirectory

            if (isDir) {
                mIcon.setImageResource(R.drawable.ic_folder_file_picker)
                mCbItem.visibility = if (isSkipDir) View.GONE else View.VISIBLE
                return
            }

            val resId: Int = itemImpl.fileType?.fileIconResId ?: R.drawable.ic_unknown_file_picker
            mIcon.setImageResource(resId)
        }

    }

    companion object {
        const val DEFAULT_FILE_TYPE = 10001
    }
}