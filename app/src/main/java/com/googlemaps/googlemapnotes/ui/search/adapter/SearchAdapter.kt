package com.googlemaps.googlemapnotes.ui.search.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.googlemaps.googlemapnotes.data.model.Notes
import com.googlemaps.googlemapnotes.ui.base.BaseAdapter
import dagger.Binds
import dagger.Provides
import javax.inject.Inject

class SearchAdapter(
    parentLifecycle: Lifecycle,
    private val notes: ArrayList<Notes>
) : BaseAdapter<Notes, SearchItemViewHolder>(parentLifecycle, notes) {

    fun clear() {
        this.notes.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SearchItemViewHolder(parent)

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

}