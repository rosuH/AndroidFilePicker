package me.rosuh.filepicker.utils

import android.view.View

interface FileListAdapterListener {
    fun onCheckSizeChanged(count: Int)
}

private typealias OnCheck = (isCheck: Boolean, view: View, pos: Int) -> Unit
private typealias OnCheckSizeChanged = (count: Int) -> Unit

class FileListAdapterListenerBuilder : FileListAdapterListener {

    private var onCheck: OnCheck? = null
    private var onCheckSizeChanged: OnCheckSizeChanged? = null

    override fun onCheckSizeChanged(count: Int) {
        this.onCheckSizeChanged?.invoke(count)
    }

    fun onCheckSizeChanged(onCheckSizeChanged: OnCheckSizeChanged) {
        this.onCheckSizeChanged = onCheckSizeChanged
    }
}