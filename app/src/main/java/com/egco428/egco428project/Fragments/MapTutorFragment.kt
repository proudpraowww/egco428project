package com.egco428.egco428project.Fragments

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.egco428.egco428project.Model.Member

import com.egco428.egco428project.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.GoogleMap

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File


class MapTutorFragment: Fragment(), OnMapReadyCallback {

    private var rootView: View? = null
    private var ERROR_DIALOG_REQUEST:Int = 9001
    lateinit var mapFragment: SupportMapFragment
    lateinit var personalData: Member
    lateinit var currentUserUid: String

    lateinit var database: DatabaseReference

    lateinit var storageReference: StorageReference
    lateinit var storage: FirebaseStorage

    lateinit var mGoogleMap: GoogleMap
    lateinit var userMarker: Marker
    private var checkMarker: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_tutor_map, container, false)

        if (isServicesOK()){
            println("Service Working")

            database = FirebaseDatabase.getInstance().getReference("Members")

            storage = FirebaseStorage.getInstance()
            storageReference = storage.reference

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

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        addMarkerFromFireBase(mGoogleMap)
    }

    private fun addMarkerFromFireBase(mGoogleMap: GoogleMap){
        database.child(currentUserUid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                personalData = Member(
                        dataSnapshot.child("id").value.toString(),
                        dataSnapshot.child("email").value.toString(),
                        dataSnapshot.child("password").value.toString(),
                        dataSnapshot.child("name").value.toString(),
                        dataSnapshot.child("lastname").value.toString(),
                        dataSnapshot.child("status").value.toString(),
                        dataSnapshot.child("phone").value.toString(),
                        dataSnapshot.child("school").value.toString(),
                        dataSnapshot.child("statusOnOff").value.toString(),
                        dataSnapshot.child("latitude").value.toString(),
                        dataSnapshot.child("longitude").value.toString(),
                        dataSnapshot.child("credit").value.toString(),
                        dataSnapshot.child("subject").value.toString(),
                        dataSnapshot.child("course_price").value.toString(),
                        dataSnapshot.child("study_status").value.toString())

                println("========================================================")
                println(personalData.latitude  + personalData.longitude)
                println(personalData.name)
                var currentLocation : LatLng = LatLng(personalData.latitude.toDouble(), personalData.longitude.toDouble())

                var bitmapDefault = BitmapFactory.decodeResource(resources, R.drawable.tutor)
                var resizeBitmap: Bitmap =  Bitmap.createScaledBitmap(bitmapDefault, 140, 140, false)

                makeUserMarkerCurrentLocation(mGoogleMap, currentLocation)
                userMarker = mGoogleMap.addMarker(MarkerOptions().position(LatLng(personalData.latitude.toDouble(), personalData.longitude.toDouble())).title("marker").icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap)))
                userMarker.setTag(personalData)
               // makeUserMarkerCurrentLocation(googleMap)

            }

            override fun onCancelled(error: DatabaseError) {
            // Failed to read
            }
        })
    }

    private fun makeUserMarkerCurrentLocation(googleMap: GoogleMap, currentLocation: LatLng){
        if (checkMarker == 0){
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 5f))
        }
        if(checkMarker > 0){
            userMarker.remove()
        }
        checkMarker = 1
    }

}
