package com.googlemaps.googlemapnotes.ui.map

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.googlemaps.googlemapnotes.R
import com.googlemaps.googlemapnotes.data.model.LocationModel
import com.googlemaps.googlemapnotes.data.model.Notes
import com.googlemaps.googlemapnotes.di.component.ActivityComponent
import com.googlemaps.googlemapnotes.ui.base.BaseActivity
import com.googlemaps.googlemapnotes.ui.home.HomeActivity
import com.googlemaps.googlemapnotes.ui.notes.NotesFragment
import com.googlemaps.googlemapnotes.ui.search.SearchFragment
import com.googlemaps.googlemapnotes.utils.AddressFormatter
import kotlinx.android.synthetic.main.bottom_sheet.*

class GoogleMapActivity : BaseActivity<SharedViewModel>(), OnMapReadyCallback {

    lateinit var mGoogleMap: GoogleMap
    var mapFrag: SupportMapFragment? = null
    lateinit var mLocationRequest: LocationRequest
    var mLastLocation: Location? = null
    internal var mCurrLocationMarker: Marker? = null
    internal var mFusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var geocoder: Geocoder
    private lateinit var notes: Notes
    val markers = mutableListOf<MarkerOptions>()
    val markerOptions = MarkerOptions()

    private val hideExpandedMenuListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.getParcelableExtra<Notes>(CHECK_NOTE) != null) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

        }
    }
    private val displayOnMapBroadcastListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val noteBroadcast = intent?.getParcelableExtra<Notes>(CHECK_NOTE)
            notes = Notes(
                noteBroadcast!!.latitude,
                noteBroadcast.longitude,
                noteBroadcast.text,
                noteBroadcast.user
            )
            checkLocationPermission()
            mFusedLocationClient?.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
            displayNoteOnMap(notes)
        }
    }

    internal var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()
                LocationModel(location.latitude, location.longitude)

                Log.i(
                    "MapsActivity",
                    "Location: " + location.getLatitude() + " " + location.getLongitude()
                )
                mLastLocation = location
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker?.remove()
                }

                //Place current location marker
                val latLng = LatLng(location.latitude, location.longitude)
                val addressFormatter = AddressFormatter(geocoder)
                val currentLocationName = addressFormatter.getAddress(
                    location.latitude,
                    location.longitude
                )
                if (currentLocationName != null) {
                    viewModel.currentLocation.postValue(currentLocationName)
                }
                viewModel.locationLatLong.postValue(
                    LocationModel(location.latitude, location.longitude)
                )
                markerOptions.position(latLng)
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions)
                mGoogleMap?.addMarker(markerOptions)?.showInfoWindow()
                markers.add(markerOptions)

                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11.0F))
            }
        }
    }

    override fun setupObservers() {
        super.setupObservers()

    }

    fun displayNoteOnMap(note: Notes) {


        val currentPosition = LatLng(note.latitude, note.longitude)
        markerOptions.position(currentPosition)
        markerOptions.title(note.text)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        mGoogleMap.addMarker(markerOptions)
        mGoogleMap?.addMarker(markerOptions)?.showInfoWindow()
        mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 11.0F))
        markers.add(markerOptions)
    }

    override fun provideLayoutId(): Int = R.layout.activity_maps

    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun setupView(savedInstanceState: Bundle?) {

        supportActionBar?.title = "Landmark Remark"

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this)

        mapFrag = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFrag?.getMapAsync(this)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_add_note -> {

                bottomSheetBehavior.setBottomSheetCallback(object :
                    BottomSheetBehavior.BottomSheetCallback() {

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        // handle onSlide
                    }

                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        when (newState) {

                            //bottom sheet states
                        }
                    }
                })
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                } else {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    replaceBottomFragment(NotesFragment())
                }

                true
            }
            R.id.navigation_search_notes -> {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                } else {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    replaceBottomFragment(SearchFragment())

                }

                true
            }
            R.id.navigation_signout -> {
                viewModel.signout()
                startActivity(Intent(applicationContext, HomeActivity::class.java))
                finish()
            }
            else -> true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        LocalBroadcastManager
            .getInstance(this)
            .registerReceiver(displayOnMapBroadcastListener, IntentFilter(DISPLAY_LOCATION))
    }

    public override fun onPause() {
        super.onPause()

        //stop location updates when Activity is no longer active
        mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(hideExpandedMenuListener)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(displayOnMapBroadcastListener)

    }


    private fun replaceBottomFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.bottomSheetContainer, fragment, TAG)
            .addToBackStack(TAG)
            .commit()


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 120000 // two minute interval
        mLocationRequest.fastestInterval = 120000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //Location Permission already granted
                mFusedLocationClient?.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    Looper.myLooper()
                )
                mGoogleMap.isMyLocationEnabled = true
            } else {
                //Request Location Permission
                checkLocationPermission()
            }
        } else {
            mFusedLocationClient?.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                Looper.myLooper()
            )
            mGoogleMap.isMyLocationEnabled = true
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            this@GoogleMapActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    }
                    .create()
                    .show()


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        }
        LocalBroadcastManager
            .getInstance(this)
            .registerReceiver(hideExpandedMenuListener, IntentFilter(DISPLAY_LOCATION))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        mFusedLocationClient?.requestLocationUpdates(
                            mLocationRequest,
                            mLocationCallback,
                            Looper.myLooper()
                        )
                        mGoogleMap.setMyLocationEnabled(true)
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    companion object {
        val MY_PERMISSIONS_REQUEST_LOCATION = 99
        const val DISPLAY_LOCATION = "display_location"
        const val CHECK_NOTE = "note"
        const val TAG = "MapActivity"

    }

    override fun onBackPressed() {

        var count = supportFragmentManager.backStackEntryCount
        if (count >= 1) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            supportFragmentManager.popBackStack()
        } else {

            super.onBackPressed()
        }


    }

}