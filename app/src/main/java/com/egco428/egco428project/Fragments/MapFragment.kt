package com.egco428.egco428project.Fragments

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.egco428.egco428project.DataProvider

import com.egco428.egco428project.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.GoogleMap

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.egco428.egco428project.R.id.lng
import com.egco428.egco428project.R.id.lat
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.egco428.egco428project.R.id.lng
import com.egco428.egco428project.R.id.lat
import kotlinx.android.synthetic.main.info_window.view.*
import com.egco428.egco428project.LocationLatLng

class MapFragment: Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private var rootView: View? = null
    private var ERROR_DIALOG_REQUEST:Int = 9001
    lateinit var mapFragment: SupportMapFragment
    lateinit var data: ArrayList<LocationLatLng>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_map, container, false)
        data = DataProvider.getData()

        if (isServicesOK()){
            Toast.makeText(this.activity,"Service Working", Toast.LENGTH_SHORT).show()
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

    override fun onMapReady(googleMap: GoogleMap) {
//        val sydney = LatLng(-33.852, 151.211)
//        mPerth = googleMap.addMarker(MarkerOptions().position(sydney)
//                .title("Marker in Sydney")
//                .snippet("snippp"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        val data = DataProvider.getData()
        for (i in data){
//            googleMap!!.addMarker(MarkerOptions().position(LatLng(i.lat, i.lng)))
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(i.lat, i.lng), 10F))
            googleMap.addMarker(MarkerOptions().position(LatLng(i.lat, i.lng)).title("Marker")).setTag(i)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(i.lat, i.lng), 10F))
        }

        googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {

            override fun getInfoWindow(marker: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                val v = layoutInflater.inflate(R.layout.info_window, null)

                val msg = v.findViewById(R.id.message) as TextView
                val lat = v.findViewById(R.id.lat) as TextView
                val lng = v.findViewById(R.id.lng) as TextView

                var dataX : LocationLatLng = marker.getTag() as LocationLatLng
                msg.text = dataX.msg
                lat.text = "Latitude: " + marker.position.latitude
                lng.text = "Longitude: " + marker.position.longitude

                return v
            }

        })
        googleMap.setOnInfoWindowClickListener(this)
    }

    override fun onInfoWindowClick(marker: Marker) {
        var dataX : LocationLatLng = marker.getTag() as LocationLatLng
//        Toast.makeText(this.activity ,dataX.id.toString() + dataX.msg,   Toast.LENGTH_SHORT).show()

        var detailDialog = AlertDialog.Builder(this.activity!!).create()
        val view = layoutInflater.inflate(R.layout.dialog_info_googlemap, null) as View
        detailDialog.setView(view)

        val idText = view.findViewById<View>(R.id.idText) as TextView
        val messageText = view.findViewById<View>(R.id.messageText) as TextView
        val latLng = view.findViewById<View>(R.id.latLng) as TextView

        val requestBtn = view.findViewById<View>(R.id.requestBtn) as Button
        val cancelBtn = view.findViewById<View>(R.id.cancelBtn) as Button

        var latlong:String = dataX.lat.toString()+ " : "+ dataX.lng.toString()

        idText.text = dataX.id.toString()
        messageText.text = dataX.msg
        latLng.text = latlong

        requestBtn.setOnClickListener {


            detailDialog.dismiss()
        }

        cancelBtn.setOnClickListener{
            detailDialog.dismiss()
        }
        detailDialog.show()
    }
}
