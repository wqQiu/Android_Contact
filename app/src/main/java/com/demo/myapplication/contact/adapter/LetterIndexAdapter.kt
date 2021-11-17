package com.demo.myapplication.contact.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.myapplication.R
import kotlinx.android.synthetic.main.item_letter_index.view.*

/**
 * created by wangwq on 2021/11/5
 */
class LetterIndexAdapter : RecyclerView.Adapter<LetterIndexAdapter.VH>() {

  private var array :Array<String> = arrayOf()

  fun setData(array: Array<String>){
    this.array = array
    notifyDataSetChanged()
  }

  class VH(itemView: View) : RecyclerView.ViewHolder(itemView)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_letter_index,parent,false))
  }

  override fun onBindViewHolder(holder: VH, position: Int) {
    holder.itemView.mTvLetter.text = array[position]
  }

  override fun getItemCount(): Int {
    return array.size
  }
}