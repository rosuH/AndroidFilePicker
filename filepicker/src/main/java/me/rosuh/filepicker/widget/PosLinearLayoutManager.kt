package me.rosuh.filepicker.widget

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet

class PosLinearLayoutManager : androidx.recyclerview.widget.LinearLayoutManager {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    private var pendingTargetPos = -1

    private var pendingPosOffset = -1

    override fun onLayoutChildren(
        recycler: androidx.recyclerview.widget.RecyclerView.Recycler?,
        state: androidx.recyclerview.widget.RecyclerView.State?
    ) {
        if (pendingTargetPos != -1 && state?.itemCount ?: 0 > 0) {
            scrollToPositionWithOffset(pendingTargetPos, pendingPosOffset)
            pendingPosOffset = -1
            pendingTargetPos = -1
        }
        super.onLayoutChildren(recycler, state)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        pendingTargetPos = -1
        pendingPosOffset = -1
        super.onRestoreInstanceState(state)
    }

    fun setTargetPos(pos: Int, offset: Int) {
        pendingTargetPos = pos
        pendingPosOffset = offset
    }
}