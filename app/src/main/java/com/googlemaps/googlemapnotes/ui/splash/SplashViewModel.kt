package com.googlemaps.googlemapnotes.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.googlemaps.googlemapnotes.data.repository.FirebaseRepository
import com.googlemaps.googlemapnotes.ui.base.BaseViewModel
import com.googlemaps.googlemapnotes.utils.common.Event
import com.googlemaps.googlemapnotes.utils.network.NetworkHelper
import com.googlemaps.googlemapnotes.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class SplashViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    val repository: FirebaseRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    val launchLocation: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()
    val launchLogin: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()

    override fun onCreate() {

    }

    fun getCurrentUser() {
        // Empty map of key and serialized value is passed to Activity in Event that is needed by the other Activity
        compositeDisposable.addAll(
            repository.getCurrentUser()
                .subscribeOn(schedulerProvider.io())
                .subscribe({
                    if (it)
                        launchLocation.postValue(Event(emptyMap()))
                    else {
                        launchLogin.postValue(Event(emptyMap()))
                    }
                }, {

                }
                )
        )
    }
}
