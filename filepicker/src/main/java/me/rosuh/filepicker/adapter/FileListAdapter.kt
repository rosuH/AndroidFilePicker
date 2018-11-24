package me.rosuh.filepicker.adapter

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import me.rosuh.filepicker.R
import me.rosuh.filepicker.bean.FileItemBean
import me.rosuh.filepicker.bean.FileTypeEnum.*

/**
 *
 * @author rosu
 * @date 2018/11/21
 */
class FileListAdapter(layoutResId: Int, var data: ArrayList<FileItemBean>) :
    BaseQuickAdapter<FileItemBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder?, item: FileItemBean?) {
        helper!!.setText(R.id.tv_list_file_picker, item!!.fileName)
        val icon = helper.getView<ImageView>(R.id.iv_icon_list_file_picker)
        val checkBox = helper.getView<CheckBox>(R.id.cb_list_file_picker)
        checkBox.isChecked = item.isChecked
        checkBox.visibility = View.VISIBLE
        when(item.fileType){
            IMAGE -> {
                icon.setImageResource(R.drawable.ic_image)
                helper.addOnClickListener(R.id.cb_list_file_picker)
            }
            DIR -> {
                icon.setImageResource(R.drawable.ic_folder)
                checkBox.visibility = View.INVISIBLE
            }
            VIDEO -> {
                icon.setImageResource(R.drawable.ic_video)
                helper.addOnClickListener(R.id.cb_list_file_picker)
            }
            COMPRESSED -> {
                icon.setImageResource(R.drawable.ic_compressed)
                helper.addOnClickListener(R.id.cb_list_file_picker)
            }
            else -> {
                icon.setImageResource(R.drawable.ic_unknown)
                helper.addOnClickListener(R.id.cb_list_file_picker)
            }
        }
    }
}