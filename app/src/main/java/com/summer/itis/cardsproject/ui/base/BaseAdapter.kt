package com.summer.itis.summerproject.ui.base

import android.support.annotation.CallSuper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View

import com.summer.itis.summerproject.ui.widget.EmptyStateRecyclerView

import android.support.constraint.Constraints.TAG
import com.summer.itis.summerproject.utils.Const.TAG_LOG

abstract class BaseAdapter<T, VH : RecyclerView.ViewHolder>(list: MutableList<T>) : RecyclerView.Adapter<VH>() {

    val items: MutableList<T> = ArrayList()

    private var onItemClickListener: OnItemClickListener<T>? = null

    private val listener = View.OnClickListener { view ->
        if (onItemClickListener != null) {
            val position = view.tag as Int
            val item = items[position]
            onItemClickListener!!.onItemClick(item)
        }
    }

    private var recyclerView: EmptyStateRecyclerView? = null

    /*fun getItems(): MutableList<T> {
        return items
    }*/

    init {
        this.items.addAll(list)
    }

    fun attachToRecyclerView(recyclerView: EmptyStateRecyclerView) {
        this.recyclerView = recyclerView
        this.recyclerView!!.adapter = this
        refreshRecycler()
    }

    fun add(value: T) {
        items.add(value)
        refreshRecycler()
    }

    fun changeDataSet(values: List<T>) {
        items.clear()
        Log.d(TAG_LOG, "values size = " + values.size)
        items.addAll(values)
        refreshRecycler()
    }

    fun addAll(values: List<T>) {
        for (value in values) {
            items.add(value)
            notifyItemInserted(items.size - 1)
        }
    }

    fun clear() {
        items.clear()
        refreshRecycler()
    }

    protected fun refreshRecycler() {
        notifyDataSetChanged()
        if (recyclerView != null) {
            recyclerView!!.checkIfEmpty()
        }
    }

    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemView.tag = position
        holder.itemView.setOnClickListener(listener)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener<T>?) {
        this.onItemClickListener = onItemClickListener
    }

    fun getItem(position: Int): T {
        return items[position]
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface OnItemClickListener<T> {
        fun onItemClick(item: T)
    }
}
