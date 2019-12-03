package me.rosuh.filepicker.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
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
class FileListAdapter(
    private val activity: FilePickerActivity,
    var dataList: ArrayList<FileItemBeanImpl>?,
    private var isSingleChoice: Boolean = FilePickerManager.config.singleChoice
) : BaseAdapter() {
    private var latestChoicePos = -1
    private lateinit var recyclerView: RecyclerView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (parent is RecyclerView) {
            recyclerView = parent
        }
        return when (isSingleChoice) {
            true -> {
                FileListItemSingleChoiceHolder(
                    LayoutInflater.from(activity).inflate(
                        R.layout.item_single_choise_list_file_picker,
                        parent,
                        false
                    )
                )
            }
            else -> {
                FileListItemHolder(
                    LayoutInflater.from(activity).inflate(
                        R.layout.item_list_file_picker,
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 10
    }

    override fun getItemViewType(position: Int): Int {
        return DEFAULT_FILE_TYPE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BaseViewHolder).bind(dataList!![position], position)
    }

    override fun getItem(position: Int): FileItemBeanImpl? {
        if (position >= 0 &&
            position < dataList!!.size &&
            getItemViewType(position) == DEFAULT_FILE_TYPE
        ) return dataList!![position]
        return null
    }

    fun disCheck(position: Int, view: View) {
        getItem(position)?.let {
            it.setCheck(false)
            view.findViewById<CheckBox>(R.id.cb_list_file_picker).isChecked = false
        }
    }

    fun check(position: Int, view: View) {
        if (isSingleChoice) {
            singleCheck(position, view)
        } else {
            multipleCheck(position, view)
        }
    }

    /*--------------------------OutSide call method begin------------------------------*/

    private fun multipleCheck(position: Int, view: View) {
        getItem(position)?.let {
            it.setCheck(true)
            view.findViewById<CheckBox>(R.id.cb_list_file_picker).isChecked = true
        }
    }

    private fun singleCheck(position: Int, view: View) {
        val rb = view.findViewById<RadioButton>(R.id.rb_list_file_picker)
        when (latestChoicePos) {
            -1 -> {
                // 从未选中过
                getItem(position)?.let {
                    it.setCheck(true)
                    rb.isChecked = true
                }
                latestChoicePos = position
            }
            position -> {
                // 取消选中
                getItem(latestChoicePos)?.let {
                    it.setCheck(false)
                    rb.isChecked = false
                }
                latestChoicePos = -1
            }
            else -> {
                // 选中新项
                getItem(latestChoicePos)?.let {
                    it.setCheck(false)
                    recyclerView.findViewHolderForAdapterPosition(latestChoicePos)
                        ?.itemView
                        ?.findViewById<RadioButton>(R.id.rb_list_file_picker)
                        ?.isChecked = false
                }
                latestChoicePos = position
                getItem(latestChoicePos)?.let {
                    it.setCheck(true)
                    rb.isChecked = true
                }
            }
        }
    }

    fun disCheckAll() {
        dataList?.forEachIndexed { index, item ->
            if (!(FilePickerManager.config.isSkipDir && item.isDir) && item.isChecked()) {
                val itemView = recyclerView.findViewHolderForAdapterPosition(index)?.itemView
                if (isSingleChoice) {
                    itemView?.findViewById<RadioButton>(R.id.rb_list_file_picker)?.let {
                        it.isChecked = false
                    }
                } else {
                    itemView?.findViewById<CheckBox>(R.id.cb_list_file_picker)?.let {
                        it.isChecked = false
                    }
                }
                item.setCheck(false)
            }
        }
    }

    fun checkAll(hadSelectedCount: Int) {
        var checkCount = hadSelectedCount
        dataList?.forEachIndexed { index, item ->
            if (checkCount >= FilePickerManager.config.maxSelectable){
                return
            }
            if (!(FilePickerManager.config.isSkipDir && item.isDir) && !item.isChecked()) {
                val itemView = recyclerView.findViewHolderForAdapterPosition(index)?.itemView
                if (isSingleChoice) {
                    itemView?.findViewById<RadioButton>(R.id.rb_list_file_picker)?.let {
                        it.isChecked = true
                    }
                } else {
                    itemView?.findViewById<CheckBox>(R.id.cb_list_file_picker)?.let {
                        it.isChecked = true
                    }
                }
                item.setCheck(true)
                checkCount++
            }
        }
    }

    /*--------------------------OutSide call method end------------------------------*/

    /*--------------------------ViewHolder Begin------------------------------*/

    abstract inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(itemImpl: FileItemBeanImpl, position: Int)
    }

    /**
     * Single choice view holder
     */
    inner class FileListItemSingleChoiceHolder(itemView: View) :
        BaseViewHolder(itemView) {

        private val isSkipDir: Boolean = FilePickerManager.config.isSkipDir
        private val mTvFileName = itemView.findViewById<TextView>(R.id.tv_list_file_picker)!!
        private val mRbItem = itemView.findViewById<RadioButton>(R.id.rb_list_file_picker)!!
        private val mIcon = itemView.findViewById<ImageView>(R.id.iv_icon_list_file_picker)!!
        private var mItemBeanImpl: FileItemBeanImpl? = null
        private var mPosition: Int? = null


        override fun bind(itemImpl: FileItemBeanImpl, position: Int) {
            mItemBeanImpl = itemImpl
            mPosition = position

            mTvFileName.text = itemImpl.fileName
            mRbItem.isChecked = itemImpl.isChecked()
            mRbItem.visibility = View.VISIBLE

            val isDir = File(itemImpl.filePath).isDirectory

            if (isDir) {
                mIcon.setImageResource(R.drawable.ic_folder_file_picker)
                mRbItem.visibility = if (isSkipDir) View.GONE else View.VISIBLE
                return
            }

            val resId: Int = itemImpl.fileType?.fileIconResId ?: R.drawable.ic_unknown_file_picker
            mIcon.setImageResource(resId)
        }

    }

    /**
     * Multiple choice view holder
     */
    inner class FileListItemHolder(itemView: View) :
        BaseViewHolder(itemView) {

        private val isSkipDir: Boolean = FilePickerManager.config.isSkipDir
        private val mTvFileName = itemView.findViewById<TextView>(R.id.tv_list_file_picker)!!
        private val mCbItem = itemView.findViewById<CheckBox>(R.id.cb_list_file_picker)!!
        private val mIcon = itemView.findViewById<ImageView>(R.id.iv_icon_list_file_picker)!!
        private var mItemBeanImpl: FileItemBeanImpl? = null
        private var mPosition: Int? = null


        override fun bind(itemImpl: FileItemBeanImpl, position: Int) {
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

    /*--------------------------ViewHolder End------------------------------*/

    companion object {
        const val DEFAULT_FILE_TYPE = 10001
    }
}