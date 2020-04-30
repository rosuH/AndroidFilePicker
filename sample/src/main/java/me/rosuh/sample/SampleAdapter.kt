package me.rosuh.sample

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

/**
 *
 * @author rosu
 * @date 2018/11/23
 */
class SampleAdapter(private val layoutInflater: LayoutInflater, private val data: ArrayList<String>) :
    androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        pos: Int
    ): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return SampleViewHolder(layoutInflater, parent)
    }

    override fun onBindViewHolder(
        holder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
        pos: Int
    ) {
        (holder as SampleViewHolder).bind(data[pos])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class SampleViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(
            inflater.inflate(
                android.R.layout.simple_list_item_1,
                parent,
                false
            )
        ) {

        fun bind(item:String){
            val tv = itemView.findViewById<TextView>(android.R.id.text1)
            tv.text = item
        }
    }
}