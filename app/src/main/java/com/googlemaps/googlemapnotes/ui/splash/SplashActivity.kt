package com.googlemaps.googlemapnotes.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.googlemaps.googlemapnotes.R
import com.googlemaps.googlemapnotes.di.component.ActivityComponent
import com.googlemaps.googlemapnotes.ui.base.BaseActivity
import com.googlemaps.googlemapnotes.ui.home.HomeActivity

import com.googlemaps.googlemapnotes.ui.map.GoogleMapActivity
import com.googlemaps.googlemapnotes.ui.signup.SignupActivity
import com.googlemaps.googlemapnotes.utils.common.Event

class SplashActivity : BaseActivity<SplashViewModel>() {


    override fun setupObservers() {
        super.setupObservers()
        // Event is used by the view model to tell the activity to launch another activity
        // view model also provided the Bundle in the event that is needed for the Activity
        viewModel.launchLogin.observe(this, Observer<Event<Map<String, String>>> {
            it.getIfNotHandled()?.run {
                finish()
                startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
            }
        })

        viewModel.launchLocation.observe(this, Observer<Event<Map<String, String>>> {
            it.getIfNotHandled()?.run {
                finish()
                startActivity(Intent(this@SplashActivity, GoogleMapActivity::class.java))
            }
        })
    }

    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        viewModel.getCurrentUser()
    }

    override fun provideLayoutId(): Int = R.layout.activity_splash

}