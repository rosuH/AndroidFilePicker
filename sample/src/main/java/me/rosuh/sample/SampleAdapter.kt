package me.rosuh.sample

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

/**
 *
 * @author rosu
 * @date 2018/11/23
 */
class SampleAdapter(private val layoutInflater: LayoutInflater, private val data: ArrayList<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): RecyclerView.ViewHolder {
        return SampleViewHolder(layoutInflater, parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        (holder as SampleViewHolder).bind(data[pos])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class SampleViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(android.R.layout.simple_list_item_1, parent, false)){

        fun bind(item:String){
            val tv = itemView.findViewById<TextView>(android.R.id.text1)
            tv.text = item
        }
    }
}