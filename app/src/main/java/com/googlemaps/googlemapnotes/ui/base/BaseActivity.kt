package com.googlemaps.googlemapnotes.ui.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.googlemaps.googlemapnotes.MapApplication
import com.googlemaps.googlemapnotes.di.component.ActivityComponent
import com.googlemaps.googlemapnotes.di.component.DaggerActivityComponent
import com.googlemaps.googlemapnotes.di.module.ActivityModule
import com.googlemaps.googlemapnotes.utils.display.Toaster
import javax.inject.Inject
/*Principles:
* Seperation of concern - seperate appplication into different components whose rules are defined and reuse them
wherever possible
* No hard dependency - It can be acheived through dagger
* all dependencies should be provided from outside */

/*
* Reduces boiler plate code
* write setup codes for dagger , observables
* It reduces code duplication
* Using generics - any paramter type would be passed to the class also we can define a boundary here*/

/*create new INstance factory to supply required dependencies such as composite diposible, n/w helper etc*/

// Made it abstact to make the base Activity not to be instantiated by other class
//Used generic to pass any viewmodel class which inherit baseviewmodel which brings type safety in the code
//Basically BaseActivity will take any class that extends BaseViewModel
abstract class BaseActivity<VM: BaseViewModel>: AppCompatActivity() {

    //get this from dagger
    @Inject
    lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        //first get dependencies before calling oncreate for safety and avoid production bugs
        injectDependencies(buildActivityComponent())
        super.onCreate(savedInstanceState)
        setContentView(provideLayoutId())
        setupObservers()
        setupView(savedInstanceState)
        //send this event to the viewmodel
        viewModel.onCreate()
    }
//when this function is called instance of activity component is created and supplied to this function
    private fun buildActivityComponent() =
        DaggerActivityComponent
            .builder()
            .applicationComponent((application as MapApplication).applicationComponent)
            .activityModule(ActivityModule(this))
            .build()

    protected open fun setupObservers() {
        viewModel.messageString.observe(this, Observer {
            it.data?.run { showMessage(this) }
        })

        viewModel.messageStringId.observe(this, Observer {
            it.data?.run { showMessage(this) }
        })
    }

    fun showMessage(message: String) = Toaster.show(applicationContext, message)

    fun showMessage(@StringRes resId: Int) = showMessage(getString(resId))

    open fun goBack() = onBackPressed()

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStackImmediate()
        else super.onBackPressed()
    }

    //provide layout id from outside
    //added lint check to make sure we pass only id here
    @LayoutRes
    protected abstract fun provideLayoutId(): Int

    protected abstract fun injectDependencies(activityComponent: ActivityComponent)

    protected abstract fun setupView(savedInstanceState: Bundle?)
}