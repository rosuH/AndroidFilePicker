package me.rosuh.filepicker.utils

import android.view.View

interface FileListAdapterListener {
    fun onCheckSizeChanged(count: Int)

    fun canCheck(pos: Int, count: Int): Boolean {
        return true
    }

    fun reachMaxCount() {
        return
    }
}

private typealias OnCheckSizeChanged = (count: Int) -> Unit
private typealias CanCheck = (pos: Int, count: Int) -> Boolean
private typealias ReachMaxCount = () -> Unit

class FileListAdapterListenerBuilder : FileListAdapterListener {

    private var onCheckSizeChanged: OnCheckSizeChanged? = null
    private var canCheck: CanCheck? = null
    private var reachMaxCount: ReachMaxCount? = null

    override fun onCheckSizeChanged(count: Int) {
        this.onCheckSizeChanged?.invoke(count)
    }

    override fun canCheck(pos: Int, count: Int): Boolean {
        return this.canCheck?.invoke(pos, count) ?: true
    }

    override fun reachMaxCount() {
        this.reachMaxCount?.invoke()
    }

    fun onCheckSizeChanged(onCheckSizeChanged: OnCheckSizeChanged) {
        this.onCheckSizeChanged = onCheckSizeChanged
    }

    fun canCheck(canCheck: CanCheck) {
        this.canCheck = canCheck
    }

    fun reachMaxCount(reachMaxCount: ReachMaxCount) {
        this.reachMaxCount = reachMaxCount
    }
}