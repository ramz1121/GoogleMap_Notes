package com.googlemaps.googlemapnotes.utils


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Singleton
import kotlin.reflect.KClass

/**
 * When ViewModel require parameters in the constructor then ViewModelProviders.of(activity).get(ViewModel.class) do not work
 * In this case we need to provide our own ViewModelProvider's Factory.
 * create method is called by Android Framework when it needs to create a ViewModel instance.
 * NOTE: When activity rotates then create method is not called but earlier instance of ViewModel is returned.
 * that is why creator is provided here so that Android Framework can create the ViewModel instance according to its need.
 * @T: It says that the ViewModelProviderFactory works with variable of type SplashViewModel
 * Example: T -> SplashViewModel,
 *
 */

/**********Notes by Ramya:

* viewmodel-->AndroidViemodel-->application in constructor
         -->Viewmodel --> empty constructor
* I use viewmodelProviders to create the instance of viewmodel using .get(ExampleViewModel::class.java)
* when I call ViewModelProviders.of(this) --> viewmodelstore uses "map" which has a "key "(key may be a activity/fragment)
  and "viewmodel"
* key --> to check activity/frag exist or not. if exist no new viewmodel is created and
  exixiting instance is returned if we rotate the screen for example.
* viewmodel store uses factory(It is a Interface) to create a new instance of view model using newInstance factory create
 using java reflection
* our case viewmodel requires other parameters in the constructor. so we cannot use default .get()
* so I created our own factory and use it in the Module to provide it whenever required
 whose constructor is different from system viewmodel factory.


/* steps followed here*/
*Here we are extending ViewModelProvider.NewInstanceFactory() and overiding the create method
 * here we have two parameters to be passed, one is type of viewmodel class and a creater which is a lamda
 * in create method we pass the class, first check this create method is called to
 return the instance of class we have to create. if we can create the instance, we are calling the creator function
 which returns the instance of viewmodel
 else we through exception
* */
@Singleton
class ViewModelProviderFactory<T : ViewModel>(
    private val kClass: KClass<T>, // KClass is the holder of class of type ViewModel that needs to be injected
    private val creator: () -> T // This is the Lambda function, this is provided be the ActivityModule/FragmentModule,
    // when creator lambda is called then that module creates and return the instance of ViewModel
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalArgumentException::class)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(kClass.java)) return creator() as T
        throw IllegalArgumentException("Unknown class name")
    }
}
