package me.rosuh.filepicker

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 *
 * @author rosu
 * @date 2018/11/23
 */
class SampleAdapter(layoutResId: Int, data: ArrayList<String>?) :
    BaseQuickAdapter<String, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder?, item: String?) {
        helper!!.setText(R.id.tv_demo, item)
    }
}