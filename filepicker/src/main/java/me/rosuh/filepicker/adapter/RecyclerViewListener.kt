package me.rosuh.filepicker.adapter

import android.app.Activity
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import me.rosuh.filepicker.R

/**
 *
 * @author rosu
 * @date 2018/11/29
 * 列表点击监听器，监听列表的点击并分辨出为单击、长按和子项被点击
 */
class RecyclerViewListener(val activity: Activity, val recyclerView: RecyclerView, val itemClickListener:IOnItemClickListener):
    RecyclerView.OnItemTouchListener{

    interface IOnItemClickListener {

        fun onItemClick(recyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, view: View, position: Int)

        fun onItemLongClick(recyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, view: View, position: Int)

        fun onItemChildClick(recyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, view: View, position: Int)
    }


    private var gestureDetectorCompat:GestureDetectorCompat =
        GestureDetectorCompat(recyclerView.context, ItemTouchHelperGestureListener())

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        gestureDetectorCompat.onTouchEvent(e)
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        gestureDetectorCompat.onTouchEvent(e)
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(p0: Boolean) {}

    inner class ItemTouchHelperGestureListener:GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            val childView = recyclerView.findChildViewUnder(e!!.x, e.y)
            childView?:return false
            when(childView.id){
                R.id.item_list_file_picker -> {
                    itemClickListener.onItemClick(recyclerView.adapter!!, childView, recyclerView.getChildLayoutPosition(childView))
                }
                else -> {
                    itemClickListener.onItemChildClick(recyclerView.adapter!!, childView, recyclerView.getChildLayoutPosition(childView))
                }
            }
            return true
        }

        override fun onLongPress(e: MotionEvent?) {
            val childView = recyclerView.findChildViewUnder(e!!.x, e.y)
            childView?:return
            when(childView.id){
                R.id.item_list_file_picker -> {
                    itemClickListener.onItemLongClick(recyclerView.adapter!!, childView, recyclerView.getChildLayoutPosition(childView))
                }
                else -> {
                    itemClickListener.onItemChildClick(recyclerView.adapter!!, childView, recyclerView.getChildLayoutPosition(childView))
                }
            }
        }
    }
}