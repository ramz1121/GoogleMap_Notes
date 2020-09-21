package com.googlemaps.googlemapnotes.di.module

import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.googlemaps.googlemapnotes.data.local.prefs.UserPreferences
import com.googlemaps.googlemapnotes.data.model.Notes
import com.googlemaps.googlemapnotes.data.repository.FirebaseNotesRepository
import com.googlemaps.googlemapnotes.data.repository.FirebaseRepository
import com.googlemaps.googlemapnotes.ui.base.BaseFragment
import com.googlemaps.googlemapnotes.ui.map.SharedViewModel
import com.googlemaps.googlemapnotes.ui.search.adapter.SearchAdapter
import com.googlemaps.googlemapnotes.utils.ViewModelProviderFactory
import com.googlemaps.googlemapnotes.utils.network.NetworkHelper
import com.googlemaps.googlemapnotes.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class FragmentModule(private val fragment: BaseFragment<*>) {

    @Provides
    fun provideLinearLayoutManager(): LinearLayoutManager = LinearLayoutManager(fragment.context)

    @Provides
    fun provideSharedViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        repository: FirebaseRepository,
        notesRepository: FirebaseNotesRepository,
        userPreferences: UserPreferences
    ): SharedViewModel =
        ViewModelProviders.of(fragment,
            ViewModelProviderFactory(SharedViewModel::class) {
                SharedViewModel(
                    schedulerProvider,
                    compositeDisposable,
                    networkHelper,
                    repository,
                    notesRepository,
                    userPreferences
                )
            }
        ).get(SharedViewModel::class.java)

    @Provides
    fun provideSearchAdapter() = SearchAdapter(fragment.lifecycle, ArrayList())
}