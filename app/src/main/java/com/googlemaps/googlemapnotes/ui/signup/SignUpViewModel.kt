package com.googlemaps.googlemapnotes.ui.signup

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.googlemaps.googlemapnotes.data.local.prefs.UserPreferences
import com.googlemaps.googlemapnotes.data.model.User
import com.googlemaps.googlemapnotes.data.model.UserID
import com.googlemaps.googlemapnotes.data.repository.FirebaseRepository
import com.googlemaps.googlemapnotes.ui.base.BaseViewModel
import com.googlemaps.googlemapnotes.utils.common.*
import com.googlemaps.googlemapnotes.utils.display.Toaster
import com.googlemaps.googlemapnotes.utils.network.NetworkHelper
import com.googlemaps.googlemapnotes.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class SignUpViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val repository: FirebaseRepository,
    private val userPreferences: UserPreferences
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    private val validationsList: MutableLiveData<List<Validation>> = MutableLiveData()

    val launchMap: MutableLiveData<Event<String>> = MutableLiveData()
    val statusMessage: LiveData<Event<String>>
        get() = launchMap

    val emailField: MutableLiveData<String> = MutableLiveData()
    val passwordField: MutableLiveData<String> = MutableLiveData()

    val usernameField: MutableLiveData<String> = MutableLiveData()
    val signingUp: MutableLiveData<Boolean> = MutableLiveData()

    val emailValidation: LiveData<Resource<Int>> = filterValidation(Validation.Field.EMAIL)
    val passwordValidation: LiveData<Resource<Int>> = filterValidation(Validation.Field.PASSWORD)
    val usernameValidation: LiveData<Resource<Int>> = filterValidation(Validation.Field.USERNAME)

    private fun filterValidation(field: Validation.Field) =
        Transformations.map(validationsList) {
            it.find { validation -> validation.field == field }
                ?.run { return@run this.resource }
                ?: Resource.unknown()
        }

    override fun onCreate() {

    }

    fun onEmailChange(email: String) = emailField.postValue(email)

    fun onPasswordChange(email: String) = passwordField.postValue(email)

    fun onUsernameChange(username: String) = usernameField.postValue(username)

    fun SignUp() {

        val email = emailField.value
        val password = passwordField.value
        val username = usernameField.value


        val validations = Validator.validateSignUpFields(email, password, username)
        validationsList.postValue(validations)

        if (validations.isNotEmpty() && email != null && password != null && username != null) {
            val successValidation =
                validations.filter { it.resource.status == Status.SUCCESS }
            if (successValidation.size == validations.size && (checkInternetConnectionWithMessage())) {
                signingUp.postValue(true)
                compositeDisposable.addAll(
                    repository.registerAuth(email, password)
                        .subscribeOn(schedulerProvider.io())
                        .subscribe(
                            {
                                repository.saveCurrentUser(User(email, username))
                                ChangeUserName()
                                signingUp.postValue(false)
                                launchMap.postValue(Event("User registered Successfully"))
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

    fun ChangeUserName() {
        val id = userPreferences.getUserId()
        id?.let { UserID(it) }
        val username = usernameField.value
        if (checkInternetConnection() && id != null && username != null) {
            repository.changeUIdUserName(UserID(id), username)
                .subscribeOn(schedulerProvider.io())
                .subscribe({

                }, {
                    handleNetworkError(it)
                })

        }
    }


}