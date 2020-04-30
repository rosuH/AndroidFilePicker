package me.rosuh.filepicker.adapter

import android.view.View
import me.rosuh.filepicker.bean.FileBean

abstract class BaseAdapter :
    androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {
    abstract fun getItem(position: Int): FileBean?
    abstract fun getItemView(position: Int): View?
}