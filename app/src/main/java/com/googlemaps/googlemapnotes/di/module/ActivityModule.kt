package com.googlemaps.googlemapnotes.di.module

import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.googlemaps.googlemapnotes.data.local.prefs.UserPreferences
import com.googlemaps.googlemapnotes.data.repository.FirebaseNotesRepository
import com.googlemaps.googlemapnotes.data.repository.FirebaseRepository
import com.googlemaps.googlemapnotes.ui.base.BaseActivity
import com.googlemaps.googlemapnotes.ui.login.LoginViewModel
import com.googlemaps.googlemapnotes.ui.map.SharedViewModel
import com.googlemaps.googlemapnotes.ui.signup.SignUpViewModel
import com.googlemaps.googlemapnotes.ui.splash.SplashViewModel
import com.googlemaps.googlemapnotes.utils.ViewModelProviderFactory
import com.googlemaps.googlemapnotes.utils.network.NetworkHelper
import com.googlemaps.googlemapnotes.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class ActivityModule(private val activity: BaseActivity<*>) {
    @Provides
    fun provideLinearLayoutManager(): LinearLayoutManager = LinearLayoutManager(activity)
//instead of get we supply our view model factory here
    @Provides
    fun provideSignUpViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        firebaseRepository: FirebaseRepository,userPreferences: UserPreferences
    ): SignUpViewModel = ViewModelProviders.of(
        activity, ViewModelProviderFactory(SignUpViewModel::class) {
            SignUpViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper,
                firebaseRepository,userPreferences
            )
        //this lambda creates and return SignupViewModel
        }).get(SignUpViewModel::class.java)

    @Provides
    fun provideLoginViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        repository: FirebaseRepository,userPreferences: UserPreferences
    ): LoginViewModel = ViewModelProviders.of(
        activity, ViewModelProviderFactory(LoginViewModel::class) {
            LoginViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper,
                repository,userPreferences
            )
        }).get(LoginViewModel::class.java)


   @Provides
    fun provideSharedViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,repository: FirebaseRepository,
        firebaseNotesRepository: FirebaseNotesRepository,
        userPreferences: UserPreferences
    ): SharedViewModel = ViewModelProviders.of(
        activity, ViewModelProviderFactory(SharedViewModel::class) {
           SharedViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper,repository,
               firebaseNotesRepository,userPreferences
            )
        }).get(SharedViewModel::class.java)

    @Provides
    fun provideSplashViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        repository: FirebaseRepository
    ): SplashViewModel = ViewModelProviders.of(
        activity, ViewModelProviderFactory(SplashViewModel::class) {
            SplashViewModel(schedulerProvider, compositeDisposable, networkHelper, repository)
            //this lambda creates and return SplashViewModel
        }).get(SplashViewModel::class.java)
}