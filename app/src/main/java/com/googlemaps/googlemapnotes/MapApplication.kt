package com.googlemaps.googlemapnotes

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.googlemaps.googlemapnotes.di.component.ApplicationComponent
import com.googlemaps.googlemapnotes.di.component.DaggerApplicationComponent
import com.googlemaps.googlemapnotes.di.module.ApplicationModule

class MapApplication  : Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        injectDependencies()
    }

    private fun injectDependencies() {
        applicationComponent = DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .build()
        applicationComponent.inject(this)
    }

    // Needed to replace the component with a test specific one
    fun setComponent(applicationComponent: ApplicationComponent) {
        this.applicationComponent = applicationComponent
    }
}