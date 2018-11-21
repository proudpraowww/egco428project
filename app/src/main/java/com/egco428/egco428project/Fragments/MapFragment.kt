package com.egco428.egco428project.Fragments

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.egco428.egco428project.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.GoogleMap
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.widget.ImageButton

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapFragment: Fragment(), OnMapReadyCallback {
    private var rootView: View? = null

    private var ERROR_DIALOG_REQUEST:Int = 9001
    lateinit var mapFragment: SupportMapFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_map, container, false)

        if (isServicesOK()){
            Toast.makeText(this.activity,"Service Working", Toast.LENGTH_SHORT).show()
            mapFragment = childFragmentManager.findFragmentById(R.id.gMap) as SupportMapFragment
            mapFragment.getMapAsync(this)
//            gMap = rootView!!.findViewById<Fragment> (R.id.gMap)
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

    override fun onMapReady(googleMap: GoogleMap?) {
        val sydney = LatLng(-33.852, 151.211)
        googleMap!!.addMarker(MarkerOptions().position(sydney)
                .title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

}