package com.googlemaps.googlemapnotes.utils.network

sealed class NetworkResult<out T : Any> {

    class Success<out T : Any>(val data: T) : NetworkResult<T>()

    class Error(val exception: Throwable) : NetworkResult<Nothing>()
}