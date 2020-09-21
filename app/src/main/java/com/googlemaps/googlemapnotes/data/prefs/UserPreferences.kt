package com.googlemaps.googlemapnotes.data.local.prefs

import android.content.SharedPreferences
import com.googlemaps.googlemapnotes.data.model.Notes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(private val prefs: SharedPreferences) {

    companion object {
        const val KEY_USER_ID = "PREF_KEY_USER_ID"
        const val KEY_USER_NAME = "PREF_KEY_USER_NAME"
        const val KEY_USER_EMAIL = "PREF_KEY_USER_EMAIL"
        const val KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN"
        const val KEY_LAT = "PREF_KEY_LAT"
        const val KEY_LONG = "PREF_KEY_LONG"
        const val KEY_USER = "PREF_KEY_USER"
        const val KEY_TEXT = "PREF_KEY_TEXT"

    }

    fun getLat(): String? =
        prefs.getString(KEY_LAT, null)

    fun setLat(userLat: String) =
        prefs.edit().putString(KEY_LAT, userLat).apply()

    fun getLong(): String? =
        prefs.getString(KEY_LONG, null)

    fun setLong(userLong: String) =
        prefs.edit().putString(KEY_LONG, userLong).apply()

    fun getUserClick(): String? =
        prefs.getString(KEY_USER, null)

    fun setUserClick(userId: String) =
        prefs.edit().putString(KEY_USER, userId).apply()

    fun getText(): String? =
        prefs.getString(KEY_TEXT, null)

    fun setText(userId: String) =
        prefs.edit().putString(KEY_TEXT, userId).apply()


    fun getUserId(): String? =
        prefs.getString(KEY_USER_ID, null)

    fun setUserId(userId: String) =
        prefs.edit().putString(KEY_USER_ID, userId).apply()

    fun removeUserId() =
        prefs.edit().remove(KEY_USER_ID).apply()

    fun getUserName(): String? =
        prefs.getString(KEY_USER_NAME, null)

    fun setUserName(userName: String?) =
        prefs.edit().putString(KEY_USER_NAME, userName).apply()

    fun removeUserName() =
        prefs.edit().remove(KEY_USER_NAME).apply()

    fun getUserEmail(): String? =
        prefs.getString(KEY_USER_EMAIL, null)

    fun setUserEmail(email: String) =
        prefs.edit().putString(KEY_USER_EMAIL, email).apply()

    fun removeUserEmail() =
        prefs.edit().remove(KEY_USER_EMAIL).apply()

    fun getAccessToken(): String? =
        prefs.getString(KEY_ACCESS_TOKEN, null)

    fun setAccessToken(token: String) =
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()

    fun removeAccessToken() =
        prefs.edit().remove(KEY_ACCESS_TOKEN).apply()
}