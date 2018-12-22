package com.summer.itis.cardsproject.ui.tests.test_list

import android.view.ViewGroup
import com.summer.itis.cardsproject.model.Test

import com.summer.itis.cardsproject.ui.base.BaseAdapter

class TestAdapter(items: MutableList<Test>) : BaseAdapter<Test, TestItemHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestItemHolder {
        return TestItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TestItemHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        holder.bind(item)
    }
}