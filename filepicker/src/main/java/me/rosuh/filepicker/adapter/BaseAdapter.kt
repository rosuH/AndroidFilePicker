package me.rosuh.filepicker.adapter

import android.support.v7.widget.RecyclerView
import me.rosuh.filepicker.bean.FileBean

abstract class BaseAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    abstract fun getItem(position: Int): FileBean?
}