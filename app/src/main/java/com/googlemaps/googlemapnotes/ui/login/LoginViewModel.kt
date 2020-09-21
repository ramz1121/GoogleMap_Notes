package com.googlemaps.googlemapnotes.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.googlemaps.googlemapnotes.data.local.prefs.UserPreferences
import com.googlemaps.googlemapnotes.data.model.User
import com.googlemaps.googlemapnotes.data.repository.FirebaseRepository
import com.googlemaps.googlemapnotes.ui.base.BaseViewModel
import com.googlemaps.googlemapnotes.utils.common.*
import com.googlemaps.googlemapnotes.utils.network.NetworkHelper
import com.googlemaps.googlemapnotes.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

// system manages the lifecycle so dagger is not required to manage it
// We have to provide it through the module
class LoginViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val repository: FirebaseRepository,
    private val userPreferences: UserPreferences

) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {
    private val validationsList: MutableLiveData<List<ValidationLogin>> = MutableLiveData()

    val launchMap: MutableLiveData<Event<String>> = MutableLiveData()
    val statusMessage: LiveData<Event<String>>
        get() = launchMap

    val emailField: MutableLiveData<String> = MutableLiveData()
    val passwordField: MutableLiveData<String> = MutableLiveData()
    val userNameField: MutableLiveData<String> = MutableLiveData()


    val signingUp: MutableLiveData<Boolean> = MutableLiveData()

    val emailValidation: LiveData<Resource<Int>> = filterValidation(ValidationLogin.Field.EMAIL)
    val passwordValidation: LiveData<Resource<Int>> =
        filterValidation(ValidationLogin.Field.PASSWORD)


    private fun filterValidation(field: ValidationLogin.Field) =
        Transformations.map(validationsList) {
            it.find { ValidationLogin -> ValidationLogin.field == field }
                ?.run { return@run this.resource }
                ?: Resource.unknown()
        }

    fun onEmailChange(email: String) = emailField.postValue(email)

    fun onPasswordChange(email: String) = passwordField.postValue(email)

    fun login() {
        val email = emailField.value
        val password = passwordField.value

        if (checkInternetConnectionWithMessage()) {
            val validations = Validator.validateLoginFields(email, password)
            validationsList.postValue(validations)

            if (validations.isNotEmpty() && email != null && password != null) {
                val successValidation =
                    validations.filter { it.resource.status == Status.SUCCESS }
                if (successValidation.size == validations.size && checkInternetConnectionWithMessage()) {
                    signingUp.postValue(true)
                    compositeDisposable.addAll(
                        repository.login(email, password)
                            .subscribeOn(schedulerProvider.io())
                            .subscribe(
                                {

                                    signingUp.postValue(false)
                                    launchMap.postValue(Event("Login Sucessfull"))
                                },
                                {
                                    handleNetworkError(it)
                                    signingUp.postValue(false)
                                }
                            )
                    )
                }
            }

        }

    }

    override fun onCreate() {

    }
}