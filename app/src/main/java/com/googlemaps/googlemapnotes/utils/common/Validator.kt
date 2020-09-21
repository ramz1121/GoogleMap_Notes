package com.googlemaps.googlemapnotes.utils.common

import android.util.Patterns
import android.util.Patterns.EMAIL_ADDRESS
import com.googlemaps.googlemapnotes.R
import com.googlemaps.googlemapnotes.utils.common.Validator.MIN_PASSWORD_LENGTH
import java.util.regex.Pattern

object Validator {

    private val EMAIL_ADDRESS = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    const val MIN_PASSWORD_LENGTH = 6
    const val MAX_USERNAME_LENGTH = 10
    fun validateSignUpFields(
        email: String?,
        password: String?,
        username: String?
    ): List<Validation> =
        ArrayList<Validation>().apply {

            when {
                email.isNullOrBlank() ->
                    add(
                        Validation(
                            Validation.Field.EMAIL,
                            Resource.error(R.string.email_field_empty)
                        )
                    )
                !EMAIL_ADDRESS.matcher(email).matches() ->
                    add(
                        Validation(
                            Validation.Field.EMAIL,
                            Resource.error(R.string.email_field_invalid)
                        )
                    )
                else ->
                    add(Validation(Validation.Field.EMAIL, Resource.success()))
            }
            when {
                password.isNullOrBlank() ->
                    add(
                        Validation(
                            Validation.Field.PASSWORD,
                            Resource.error(R.string.password_field_empty)
                        )
                    )
                password.length < MIN_PASSWORD_LENGTH ->
                    add(
                        Validation(
                            Validation.Field.PASSWORD,
                            Resource.error(R.string.password_field_small_length)
                        )
                    )
                else -> add(Validation(Validation.Field.PASSWORD, Resource.success()))
            }
            when {
                username.isNullOrBlank() ->
                    add(
                        Validation(
                            Validation.Field.USERNAME,
                            Resource.error(R.string.username_field_empty)
                        )
                    )
                username.length > MAX_USERNAME_LENGTH ->
                    add(
                        Validation(
                            Validation.Field.USERNAME,
                            Resource.error(R.string.username_field_invalid)
                        )
                    )
                else ->
                    add(Validation(Validation.Field.USERNAME, Resource.success()))
            }
        }

    fun validateLoginFields(
        email: String?,
        password: String?
        ): List<ValidationLogin> =
        ArrayList<ValidationLogin>().apply {

            when {
                email.isNullOrBlank() ->
                    add(
                        ValidationLogin(
                            ValidationLogin.Field.EMAIL,
                            Resource.error(R.string.email_field_empty)
                        )
                    )
                !EMAIL_ADDRESS.matcher(email).matches() ->
                    add(
                        ValidationLogin(
                            ValidationLogin.Field.EMAIL,
                            Resource.error(R.string.email_field_invalid)
                        )
                    )
                else ->
                    add(ValidationLogin(ValidationLogin.Field.EMAIL, Resource.success()))
            }
            when {
                password.isNullOrBlank() ->
                    add(
                        ValidationLogin(
                            ValidationLogin.Field.PASSWORD,
                            Resource.error(R.string.password_field_empty)
                        )
                    )
                password.length < MIN_PASSWORD_LENGTH ->
                    add(
                        ValidationLogin(
                            ValidationLogin.Field.PASSWORD,
                            Resource.error(R.string.password_field_small_length)
                        )
                    )
                else -> add(ValidationLogin(ValidationLogin.Field.PASSWORD, Resource.success()))
            }


        }
    }


    data class Validation(val field: Field, val resource: Resource<Int>) {

        enum class Field {
            EMAIL,
            PASSWORD,
            USERNAME
        }
    }
data class ValidationLogin(val field: Field, val resource: Resource<Int>) {

    enum class Field {
        EMAIL,
        PASSWORD
    }
}