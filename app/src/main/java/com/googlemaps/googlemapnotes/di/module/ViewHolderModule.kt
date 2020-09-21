package com.googlemaps.googlemapnotes.di.module

import androidx.lifecycle.LifecycleRegistry
import com.googlemaps.googlemapnotes.di.ViewModelScope
import com.googlemaps.googlemapnotes.ui.base.BaseItemViewHolder
import dagger.Module
import dagger.Provides

@Module
class ViewHolderModule(private val viewHolder: BaseItemViewHolder<*, *>) {

    @Provides
    @ViewModelScope
    fun provideLifecycleRegistry(): LifecycleRegistry = LifecycleRegistry(viewHolder)
}