package com.googlemaps.googlemapnotes.di.component

import com.googlemaps.googlemapnotes.di.FragmentScope
import com.googlemaps.googlemapnotes.di.module.FragmentModule
import com.googlemaps.googlemapnotes.ui.notes.NotesFragment
import com.googlemaps.googlemapnotes.ui.search.SearchFragment
import dagger.Component

@FragmentScope
@Component(
    dependencies = [ApplicationComponent::class],
    modules = [FragmentModule::class]
)
interface FragmentComponent {

    fun inject(fragment: NotesFragment)
    fun inject(searchFragment: SearchFragment)
}