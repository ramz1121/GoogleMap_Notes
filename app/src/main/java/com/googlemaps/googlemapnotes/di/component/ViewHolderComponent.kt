package com.googlemaps.googlemapnotes.di.component


import com.googlemaps.googlemapnotes.di.ViewModelScope
import com.googlemaps.googlemapnotes.di.module.ViewHolderModule
import com.googlemaps.googlemapnotes.ui.search.adapter.SearchItemViewHolder
import dagger.Component

@ViewModelScope
@Component(
    dependencies = [ApplicationComponent::class],
    modules = [ViewHolderModule::class]
)
interface ViewHolderComponent {

    fun inject(viewHolder: SearchItemViewHolder)
}