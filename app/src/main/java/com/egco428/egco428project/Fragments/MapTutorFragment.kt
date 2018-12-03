package com.egco428.egco428project.Fragments

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
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
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class MapTutorFragment: Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private var rootView: View? = null
    private var ERROR_DIALOG_REQUEST:Int = 9001
    lateinit var mapFragment: SupportMapFragment
    lateinit var currentUserUid: String
    lateinit var personalData: Member
    lateinit var studyPersonData: Member

    lateinit var database: DatabaseReference

    lateinit var storageReference: StorageReference
    lateinit var storage: FirebaseStorage

    lateinit var mGoogleMap: GoogleMap
    lateinit var userMarker: Marker
    lateinit var studyMarker: Marker
    private var checkMarker: Int = 0
    private var checkStudyMarker: Int = 0

    private var studyLocation = LatLng(0.0, 0.0)
    private var databaseTutorListener:ValueEventListener? = null
    private var databaseStudentListener:ValueEventListener? = null

    //set up variable, Firebase database, and check google map service
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

    //check google map service
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

    //Google Map Ready by getMapAsync Function From onCreate
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        addMarkerFromFireBase(mGoogleMap)
        addMarkerStudentInTutorMap(mGoogleMap)
        makeInfoWindowGoogleMap(mGoogleMap)
        googleMap.setOnInfoWindowClickListener(this)
    }

    //add student marker
    private fun addMarkerStudentInTutorMap(mGoogleMap: GoogleMap){

        databaseStudentListener = database.addValueEventListener(object : ValueEventListener {
            var studyPerson :String? = null
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                studyPerson = dataSnapshot.child(currentUserUid).child("study_status").value.toString()
                println(studyPerson)
                if(studyPerson == ""){
                    //Toast.makeText(activity!!.applicationContext, "No study Person right now.", Toast.LENGTH_SHORT).show()

                }else{
                    studyPersonData = Member(
                            dataSnapshot.child(studyPerson).child("id").value.toString(),
                            dataSnapshot.child(studyPerson).child("email").value.toString(),
                            dataSnapshot.child(studyPerson).child("password").value.toString(),
                            dataSnapshot.child(studyPerson).child("name").value.toString(),
                            dataSnapshot.child(studyPerson).child("lastname").value.toString(),
                            dataSnapshot.child(studyPerson).child("status").value.toString(),
                            dataSnapshot.child(studyPerson).child("phone").value.toString(),
                            dataSnapshot.child(studyPerson).child("school").value.toString(),
                            dataSnapshot.child(studyPerson).child("statusOnOff").value.toString(),
                            dataSnapshot.child(studyPerson).child("latitude").value.toString(),
                            dataSnapshot.child(studyPerson).child("longitude").value.toString(),
                            dataSnapshot.child(studyPerson).child("credit").value.toString(),
                            dataSnapshot.child(studyPerson).child("subject").value.toString(),
                            dataSnapshot.child(studyPerson).child("course_price").value.toString(),
                            dataSnapshot.child(studyPerson).child("study_status").value.toString(),
                            dataSnapshot.child(studyPerson).child("start_time").value.toString())

                    println("========================================================")
                    println(studyPersonData.latitude + studyPersonData.longitude)
                    println(studyPersonData.name)

                    studyLocation  = LatLng(studyPersonData.latitude.toDouble(), studyPersonData.longitude.toDouble())
//
                    var bitmapDefault = BitmapFactory.decodeResource(resources, R.drawable.cash100)
                    var resizeBitmap: Bitmap =  Bitmap.createScaledBitmap(bitmapDefault, 140, 140, false)

                    makeStudyMarkerCurrentLocation(mGoogleMap, studyLocation)
                    studyMarker = mGoogleMap.addMarker(MarkerOptions().position((studyLocation)).title("marker").icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap)))  //.icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap))
                    studyMarker.setTag(studyPersonData)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read
            }
        })
    }

    //add tutor marker
    private fun addMarkerFromFireBase(mGoogleMap: GoogleMap){
        databaseTutorListener = database.child(currentUserUid).addValueEventListener(object : ValueEventListener {
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
                        dataSnapshot.child("study_status").value.toString(),
                        dataSnapshot.child("start_time").value.toString())

                println("========================================================")
                println(personalData.latitude  + personalData.longitude)
                println(personalData.name)
                var currentLocation  = LatLng(personalData.latitude.toDouble(), personalData.longitude.toDouble())

//                var bitmapDefault = BitmapFactory.decodeResource(resources, R.drawable.tutor)
//                var resizeBitmap: Bitmap =  Bitmap.createScaledBitmap(bitmapDefault, 140, 140, false)

                makeUserMarkerCurrentLocation(mGoogleMap, currentLocation)
                userMarker = mGoogleMap.addMarker(MarkerOptions().position(currentLocation).title("marker").icon(BitmapDescriptorFactory.fromResource(R.drawable.teacher))) //.icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap))
                userMarker.setTag(personalData)
            }

            override fun onCancelled(error: DatabaseError) {
            // Failed to read
            }
        })
    }

    //update student marker
    private fun makeStudyMarkerCurrentLocation(googleMap: GoogleMap, currentLocation: LatLng){
//        if (checkStudyMarker == 0){
//            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 2f))
//        }
        if(checkStudyMarker > 0){
            studyMarker.remove()
        }
        checkStudyMarker = 1
    }

    //update tutor marker
    private fun makeUserMarkerCurrentLocation(googleMap: GoogleMap, currentLocation: LatLng){
        if (checkMarker == 0){
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 5f))
        }
        if(checkMarker > 0){
            userMarker.remove()
        }
        checkMarker = 1
    }

    //make custom google map infomation window
    private fun makeInfoWindowGoogleMap(googleMap: GoogleMap){
        googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {

            override fun getInfoWindow(marker: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {

                var infoData : Member = marker.getTag() as Member
                val v = layoutInflater.inflate(R.layout.info_window, null)
//                var imageProfile = v.findViewById<View>(R.id.imgProfie) as ImageView
                val fName = v.findViewById(R.id.name) as TextView
                val lName = v.findViewById(R.id.lastname) as TextView
                val status = v.findViewById(R.id.status) as TextView

//                var photoRef = storageReference.child("photo/"+dataTutor.id)
//                var localFile = File.createTempFile("images", "jpg")

                fName.text = "FirstName : " + infoData.name
                lName.text = "LastName : " + infoData.lastname
                status.text = "Status : " + infoData.status

                return v
            }
        })
    }

    //Catch event when touch google map infomation and then show detail dialog
    override fun onInfoWindowClick(marker: Marker) {
        var infoData : Member = marker.getTag() as Member
        var alreadyRequest : Int = 0

        var detailDialog = AlertDialog.Builder(this.activity!!).create()
        val view = layoutInflater.inflate(R.layout.dialog_info_googlemap, null) as View
        detailDialog.setView(view)

        val img = view.findViewById<View>(R.id.personalImg) as ImageView
        val email = view.findViewById<View>(R.id.email) as TextView
        val fName = view.findViewById<View>(R.id.name) as TextView
        val lName = view.findViewById<View>(R.id.lastName) as TextView
        val phone = view.findViewById<View>(R.id.phone) as TextView
        val status = view.findViewById<View>(R.id.status) as TextView
        val otherInfo = view.findViewById<View>(R.id.otherInfo) as TextView

        val cancelBtn = view.findViewById<View>(R.id.cancelBtn) as Button
        val requestBtn = view.findViewById<View>(R.id.requestBtn) as Button

        requestBtn.setVisibility(View.GONE)

        if (infoData.status == "student"){
            img.setImageResource(R.drawable.student)
            otherInfo.text = "Tutor : "+ infoData.subject
        }else{
            img.setImageResource(R.drawable.teacher)
            otherInfo.text = "School : "+ infoData.subject
        }
        email.text = "email : "+ infoData.email
        fName.text = "FirstName : "+ infoData.name
        lName.text = "LastName : "+ infoData.lastname
        phone.text = "Phone : "+ infoData.phone
        status.text = "Status : "+ infoData.status

        cancelBtn.setOnClickListener{
            detailDialog.dismiss()
        }
        detailDialog.show()
    }

    override fun onPause() {
        super.onPause()
        database.removeEventListener(databaseTutorListener)
        database.removeEventListener(databaseStudentListener)

    }
}
