package me.rosuh.filepicker.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import me.rosuh.filepicker.bean.FileBean

abstract class BaseAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    abstract fun getItem(position: Int): FileBean?
    abstract fun getItemView(position: Int): View?
    protected var clickListener: RecyclerViewListener.OnItemClickListener? = null

    open fun setListener(navListener: RecyclerViewListener) {
        clickListener = navListener.itemClickListener
    }
    open fun removeListener() {
        clickListener = null
    }
}