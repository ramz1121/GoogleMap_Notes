package com.googlemaps.googlemapnotes.ui.search.adapter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.googlemaps.googlemapnotes.data.local.prefs.UserPreferences
import com.googlemaps.googlemapnotes.data.model.Notes
import com.googlemaps.googlemapnotes.ui.base.BaseItemViewModel
import com.googlemaps.googlemapnotes.utils.common.Resource
import com.googlemaps.googlemapnotes.utils.network.NetworkHelper
import com.googlemaps.googlemapnotes.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SearchItemViewModel @Inject constructor(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper
) : BaseItemViewModel<Notes>(schedulerProvider, compositeDisposable, networkHelper) {
    override fun onCreate() {

    }

    val text: LiveData<String> = Transformations.map(data) { it.text }
    val latLong: LiveData<String?> = Transformations.map(data) { it.latitude.toString() + it.longitude.toString() }
    val user: LiveData<String> = Transformations.map(data) { it.user }
    val dataClicked: MutableLiveData<Notes> = MutableLiveData()

    fun onItemClick(position: Int) {
        dataClicked.postValue(data.value)

    }

}