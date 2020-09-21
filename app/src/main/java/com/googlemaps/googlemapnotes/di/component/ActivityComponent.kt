package com.googlemaps.googlemapnotes.di.component

import com.googlemaps.googlemapnotes.di.ActivityScope
import com.googlemaps.googlemapnotes.di.module.ActivityModule
import com.googlemaps.googlemapnotes.ui.home.HomeActivity
import com.googlemaps.googlemapnotes.ui.login.LoginActivity
import com.googlemaps.googlemapnotes.ui.map.GoogleMapActivity
import com.googlemaps.googlemapnotes.ui.signup.SignupActivity
import com.googlemaps.googlemapnotes.ui.splash.SplashActivity
import dagger.Component

@ActivityScope
@Component(
    dependencies = [ApplicationComponent::class],
    modules = [ActivityModule::class]
)
interface ActivityComponent {
    fun inject(activity: SignupActivity)
    fun inject(activity: SplashActivity)
    fun inject(activity: GoogleMapActivity)
    fun inject(activity:LoginActivity)
    fun inject(activity:HomeActivity)
}