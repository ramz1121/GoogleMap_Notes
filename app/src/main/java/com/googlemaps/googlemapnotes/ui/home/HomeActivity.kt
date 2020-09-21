package com.googlemaps.googlemapnotes.ui.home

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.googlemaps.googlemapnotes.MapApplication
import com.googlemaps.googlemapnotes.R
import com.googlemaps.googlemapnotes.di.component.ActivityComponent
import com.googlemaps.googlemapnotes.di.component.DaggerActivityComponent
import com.googlemaps.googlemapnotes.di.module.ActivityModule
import com.googlemaps.googlemapnotes.ui.base.BaseActivity
import com.googlemaps.googlemapnotes.ui.base.BaseViewModel
import com.googlemaps.googlemapnotes.ui.login.LoginActivity
import com.googlemaps.googlemapnotes.ui.signup.SignupActivity
import com.googlemaps.googlemapnotes.ui.splash.SplashViewModel
import com.googlemaps.googlemapnotes.utils.common.navigateTo
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity<SplashViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_home)
    }


    override fun provideLayoutId(): Int =R.layout.activity_home

    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        login.setOnClickListener {
            navigateTo(LoginActivity::class.java)
        }

        signUp.setOnClickListener {
            navigateTo(SignupActivity::class.java)
        }
    }
}