package com.googlemaps.googlemapnotes.di

import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.SOURCE)
annotation class ActivityScope

@Scope
@Retention(AnnotationRetention.SOURCE)
annotation class ViewModelScope

@Scope
@Retention(AnnotationRetention.SOURCE)
annotation class FragmentScope