package com.googlemaps.googlemapnotes.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.googlemaps.googlemapnotes.MapApplication
import com.googlemaps.googlemapnotes.data.local.prefs.UserPreferences
import com.googlemaps.googlemapnotes.data.remote.FirebaseSource
import com.googlemaps.googlemapnotes.di.ApplicationContext
import com.googlemaps.googlemapnotes.utils.network.NetworkHelper
import com.googlemaps.googlemapnotes.utils.rx.RxSchedulerProvider
import com.googlemaps.googlemapnotes.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: MapApplication) {
    @Provides
    @Singleton
    fun provideApplication(): Application = application

    @Provides
    @Singleton
    @ApplicationContext
    fun provideContext(): Context = application

    /**
     * Since this function do not have @Singleton then each time CompositeDisposable is injected
     * then a new instance of CompositeDisposable will be provided
     */

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    fun provideSchedulerProvider(): SchedulerProvider = RxSchedulerProvider()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDb(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideSharedPreferences(): SharedPreferences =
        application.getSharedPreferences("landmark-remark-project-prefs", Context.MODE_PRIVATE)


    @Singleton
    @Provides
    fun provideNetworkHelper(): NetworkHelper = NetworkHelper(application)

    @Singleton
    @Provides
    fun provideUserPreferences(): UserPreferences = UserPreferences(provideSharedPreferences())
}