package com.egco428.egco428project.Fragments

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.egco428.egco428project.DataProvider
import com.egco428.egco428project.Model.Member

import com.egco428.egco428project.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.GoogleMap

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class MapTutorFragment: Fragment(), OnMapReadyCallback {

    private var rootView: View? = null
    private var ERROR_DIALOG_REQUEST:Int = 9001
    lateinit var mapFragment: SupportMapFragment
    lateinit var dataPersonal: Member
    lateinit var currentUserUid: String

    lateinit var database: DatabaseReference

    lateinit var locationManager: LocationManager
    lateinit var locationListener: LocationListener
    private  var userCurrentLocation: LatLng = LatLng(0.0, 0.0)

    lateinit var mGoogleMap: GoogleMap
    lateinit var userMarker: Marker
    private var checkMarker: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_tutor_map, container, false)

        locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener{

            override fun onLocationChanged(location: Location?) {
                userCurrentLocation = LatLng(location!!.latitude, location!!.longitude)
                println(location!!.latitude)
                println(location!!.longitude)
                println("==========================================")
                println(userCurrentLocation.latitude)
                println(userCurrentLocation.longitude)
                makeUserMarkerCurrentLocation(mGoogleMap)

            }

            override fun onProviderDisabled(p0: String?) {
                val i = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(i)
            }

            override fun onProviderEnabled(p0: String?) {
            }

            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
            }

        }
        requestLocation()

        if (isServicesOK()){
            println("Service Working")

            database = FirebaseDatabase.getInstance().getReference("Members")

            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                currentUserUid = user.uid
            }else{
                println("current user have problem !!!")
            }

            mapFragment = childFragmentManager.findFragmentById(R.id.gMap) as SupportMapFragment
            mapFragment.getMapAsync(this)

        }else{
            Toast.makeText(this.activity,"Service not Working", Toast.LENGTH_SHORT).show()
        }
        return rootView
    }

    private fun isServicesOK(): Boolean{
        Log.d("msg from isServicesOK","checking google service")

        var available:Int = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this.activity)

        if (available == ConnectionResult.SUCCESS) {
            Log.d("msg from isServicesOK","Google Services Working")
            return true
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d("msg from isServicesOK","Error occured but can fix it")
            var dialog:Dialog = GoogleApiAvailability.getInstance().getErrorDialog(this.activity, available, ERROR_DIALOG_REQUEST)
            dialog.show()
        }else{
            Toast.makeText(this.activity, "can't make reQuest", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    private fun requestLocation(){
        if(ActivityCompat.checkSelfPermission(this.context!!, Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET),10)
            }
            return
        }
//        locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0f,locationListener)
        locationManager!!.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null)
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        when(requestCode){
//            10 -> requestLocation()
//            else -> {}
//        }
//    }

    override fun onMapReady(googleMap: GoogleMap) {
//        val sydney = LatLng(-33.852, 151.211)
//        mPerth = googleMap.addMarker(MarkerOptions().position(sydney)
//                .title("Marker in Sydney")
//                .snippet("snippp"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mGoogleMap = googleMap
//        userMarker = googleMap
    }

    private fun makeUserMarkerCurrentLocation(googleMap: GoogleMap){
        if (checkMarker == 0){
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userCurrentLocation, 5f))
        }
        if(checkMarker > 0){
            userMarker.remove()
        }
        userMarker = googleMap.addMarker(MarkerOptions().position(userCurrentLocation).title("You are here"))
        saveLocationCurrentUser(userCurrentLocation)
        checkMarker = 1
    }

    private fun saveLocationCurrentUser(userCurrentLocation: LatLng){
        database.child(currentUserUid).child("latitude").setValue(userCurrentLocation.latitude.toString())
        database.child(currentUserUid).child("longitude").setValue(userCurrentLocation.longitude.toString())
    }

}
