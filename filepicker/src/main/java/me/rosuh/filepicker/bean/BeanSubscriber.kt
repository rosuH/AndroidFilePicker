package me.rosuh.filepicker.bean

interface BeanSubscriber {
    /**
     * Update other ui when item check status changed.
     * 当 item 的选中状态改变时，通知 Activity 界面更新
     */
    fun updateItemUI(isCheck:Boolean)
}