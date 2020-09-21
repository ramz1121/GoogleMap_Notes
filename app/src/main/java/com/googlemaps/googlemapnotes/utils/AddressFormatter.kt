package com.googlemaps.googlemapnotes.utils

import android.content.Context
import android.location.Geocoder
import android.location.Location

class AddressFormatter(private val geocoder: Geocoder)  {
    private val emptyResult = ""
    private val maxResults = 1
    private val addressLineIndex = 0

     fun getAddress(lat: Double, lng: Double): String {
        val list = geocoder.getFromLocation(lat, lng, maxResults)
        if (list.isNotEmpty()) {
            return list[0].getAddressLine(0)
        }
        return emptyResult
    }
}