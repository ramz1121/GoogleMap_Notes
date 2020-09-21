package com.googlemaps.googlemapnotes.di.component

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.googlemaps.googlemapnotes.MapApplication
import com.googlemaps.googlemapnotes.data.local.prefs.UserPreferences
import com.googlemaps.googlemapnotes.data.repository.FirebaseNotesRepository
import com.googlemaps.googlemapnotes.data.repository.FirebaseRepository
import com.googlemaps.googlemapnotes.di.ActivityContext
import com.googlemaps.googlemapnotes.di.ApplicationContext
import com.googlemaps.googlemapnotes.di.module.ApplicationModule
import com.googlemaps.googlemapnotes.utils.network.NetworkHelper
import com.googlemaps.googlemapnotes.utils.rx.SchedulerProvider
import dagger.Component
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

// to make fragment module use application module through application component as the fragment module cannot use it directly
@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun inject(app: MapApplication)

    fun getApplication(): Application

    @ApplicationContext
    fun getContext(): Context

    fun getSharedPreferences(): SharedPreferences

    fun getNetworkHelper(): NetworkHelper

    fun firebaseAuth(): FirebaseAuth

    fun firebaseDatabase(): FirebaseDatabase

    /**---------------------------------------------------------------------------
     * Dagger will internally create UserRepository instance using constructor injection.
     * Dependency through constructor
     * FIrebaseRepository ->
     *  [NetworkService -> Nothing is required],
     *  [DatabaseService -> Nothing is required],
     *  [UserPreferences -> [SharedPreferences -> provided by the function provideSharedPreferences in ApplicationModule class]]
     * So, Dagger will be able to create an instance of FirebaseRepository by its own using constructor injection
     *---------------------------------------------------------------------------------
     */
    fun getSchedulerProvider(): SchedulerProvider

    fun getCompositeDisposable(): CompositeDisposable

    fun getFirebaseRepository(): FirebaseRepository

    fun getFirebaseNotesRepository(): FirebaseNotesRepository

    fun getUserPreferences(): UserPreferences

}
