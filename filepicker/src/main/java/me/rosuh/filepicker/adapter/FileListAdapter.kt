package me.rosuh.filepicker.adapter

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import me.rosuh.filepicker.R
import me.rosuh.filepicker.bean.FileItemBean
import me.rosuh.filepicker.config.FilePickerConfig
import me.rosuh.filepicker.config.FilePickerManager
import java.io.File

/**
 *
 * @author rosu
 * @date 2018/11/21
 */
class FileListAdapter(layoutResId: Int, var data: ArrayList<FileItemBean>) :
    BaseQuickAdapter<FileItemBean, BaseViewHolder>(layoutResId, data) {
    private val isSkipDir:Boolean = FilePickerConfig.getInstance(FilePickerManager.instance).isSkipDir

    override fun convert(helper: BaseViewHolder?, item: FileItemBean?) {
        helper!!.setText(R.id.tv_list_file_picker, item!!.fileName)
        val icon = helper.getView<ImageView>(R.id.iv_icon_list_file_picker)
        val checkBox = helper.getView<CheckBox>(R.id.cb_list_file_picker)
        checkBox.isChecked = item.isChecked
        checkBox.visibility = View.VISIBLE

        val isDir = File(item.filePath).isDirectory

        if (isDir) {
            icon.setImageResource(R.drawable.ic_folder)
            checkBox.visibility = if (isSkipDir)  View.INVISIBLE else View.VISIBLE
            return
        }
        helper.addOnClickListener(R.id.cb_list_file_picker)
        val resId:Int = item.fileType?.fileIconResId ?: R.drawable.ic_unknown
        icon.setImageResource(resId)
    }
}