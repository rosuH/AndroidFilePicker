package me.rosuh.filepicker.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import me.rosuh.filepicker.bean.FileNavBean
import me.rosuh.filepicker.R

/**
 *
 * @author rosu
 * @date 2018/11/21
 */
class FileNavAdapter(layoutResId: Int, data: MutableList<FileNavBean>?) :
    BaseQuickAdapter<FileNavBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder?, item: FileNavBean?) {
        helper!!
                .setText(R.id.btn_nav, item!!.dirName)
                .addOnClickListener(R.id.btn_nav)
    }
}