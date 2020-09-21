package com.googlemaps.googlemapnotes.data.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import javax.inject.Singleton


@SuppressLint("ParcelCreator")
@Parcelize
data class Notes(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var text: String? = null,
    var user: String? = null
) : Parcelable