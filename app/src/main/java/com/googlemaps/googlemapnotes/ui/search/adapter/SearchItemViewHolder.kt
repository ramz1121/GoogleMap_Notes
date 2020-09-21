package com.googlemaps.googlemapnotes.ui.search.adapter

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.model.LatLng
import com.googlemaps.googlemapnotes.R
import com.googlemaps.googlemapnotes.data.local.prefs.UserPreferences
import com.googlemaps.googlemapnotes.data.model.Notes
import com.googlemaps.googlemapnotes.di.component.ViewHolderComponent
import com.googlemaps.googlemapnotes.ui.base.BaseItemViewHolder
import com.googlemaps.googlemapnotes.ui.map.GoogleMapActivity.Companion.CHECK_NOTE
import com.googlemaps.googlemapnotes.ui.map.GoogleMapActivity.Companion.DISPLAY_LOCATION
import kotlinx.android.synthetic.main.item_note.view.*

class SearchItemViewHolder(parent: ViewGroup) :
    BaseItemViewHolder<Notes, SearchItemViewModel>(R.layout.item_note, parent) {

    override fun injectDependencies(viewHolderComponent: ViewHolderComponent) {
        viewHolderComponent.inject(this)
    }

    override fun setupView(view: View) {
        view.setOnClickListener {
            viewModel.onItemClick(adapterPosition)
            viewModel.dataClicked.observe(this, Observer {
             // Update map with new location on itemclick
                val broadcastManager = LocalBroadcastManager.getInstance(view.context)
                val intent = Intent(DISPLAY_LOCATION)
                intent.apply {
                    putExtra(CHECK_NOTE, it)
                }
                broadcastManager.sendBroadcast(intent)
            })

        }
    }


    override fun setupObservers() {
            super.setupObservers()

            viewModel.user.observe(this, Observer {
                itemView.noteUser.text = it
            })
            viewModel.text.observe(this, Observer {
                itemView.noteText.text = it
            })
            viewModel.latLong.observe(this, Observer {
                itemView.noteLocation.text = it

            })
        viewModel

        }


}