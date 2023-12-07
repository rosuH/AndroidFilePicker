package me.rosuh.filepicker.adapter

import android.content.res.Resources
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import me.rosuh.filepicker.R

/**
 *
 * @author rosu
 * @date 2018/11/29
 * 列表点击监听器，监听列表的点击并分辨出为单击、长按和子项被点击
 * OnItemTouchListener 无法轻易实现对子控件点击事件的监听
 *
 */
class RecyclerViewListener(
    val recyclerView: RecyclerView,
    val itemClickListener: OnItemClickListener
) :
    RecyclerView.OnItemTouchListener {

    /**
     * Custom item click listener, receive item event and redispatch
     */
    interface OnItemClickListener {

        /**
         * Item click
         */
        fun onItemClick(
            adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
            view: View,
            position: Int
        )

        /**
         * Item long click
         */
        fun onItemLongClick(
            adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
            view: View,
            position: Int
        )

        /**
         * Item child click
         */
        fun onItemChildClick(
            adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
            view: View,
            position: Int
        )
    }

    private var gestureDetectorCompat: GestureDetectorCompat =
        GestureDetectorCompat(recyclerView.context, ItemTouchHelperGestureListener())

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        gestureDetectorCompat.onTouchEvent(e)
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        return gestureDetectorCompat.onTouchEvent(e)
    }

    override fun onRequestDisallowInterceptTouchEvent(p0: Boolean) {}

    private val screenWidth by lazy { Resources.getSystem().displayMetrics.widthPixels }
    private val iconRight = screenWidth * 0.1370
    private val checkBoxLeft = screenWidth * (1 - 0.1370)

    inner class ItemTouchHelperGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            val childView = recyclerView.findChildViewUnder(e!!.x, e.y)
            childView ?: return false
            when (childView.id) {
                R.id.item_list_file_picker -> {
                    // 点击在 icon 上或者点击在 checkbox 上
                    if (e.x <= iconRight || e.x >= checkBoxLeft) {
                        itemClickListener.onItemChildClick(
                            recyclerView.adapter!!,
                            childView,
                            recyclerView.getChildLayoutPosition(childView)
                        )
                        return true
                    }
                    itemClickListener.onItemClick(
                        recyclerView.adapter!!,
                        childView,
                        recyclerView.getChildLayoutPosition(childView)
                    )
                }
                R.id.item_nav_file_picker -> {
                    itemClickListener.onItemClick(
                        recyclerView.adapter!!,
                        childView,
                        recyclerView.getChildLayoutPosition(childView)
                    )
                }
            }
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            val childView = recyclerView.findChildViewUnder(e!!.x, e.y)
            childView ?: return
            when (childView.id) {
                R.id.item_list_file_picker -> {
                    itemClickListener.onItemLongClick(
                        recyclerView.adapter!!,
                        childView,
                        recyclerView.getChildLayoutPosition(childView)
                    )
                }
            }
        }
    }
}