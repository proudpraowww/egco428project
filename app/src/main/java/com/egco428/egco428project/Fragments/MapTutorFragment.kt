package com.egco428.egco428project.Fragments

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationListener
import android.location.LocationManager
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
import java.io.File


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
        addMarkerStudentInTutorMap(mGoogleMap)
        makeInfoWindowGoogleMap(mGoogleMap)
        googleMap.setOnInfoWindowClickListener(this)
    }

    private fun addMarkerStudentInTutorMap(mGoogleMap: GoogleMap){
        database.addValueEventListener(object : ValueEventListener {
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
                    studyMarker = mGoogleMap.addMarker(MarkerOptions().position((studyLocation)).icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap)).title("marker"))
                    studyMarker.setTag(studyPersonData)
                }


            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read
            }
        })
    }

    private fun addMarkerFromFireBase(mGoogleMap: GoogleMap){
        database.child(currentUserUid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (isAdded){

                }
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

                /*var bitmapDefault = BitmapFactory.decodeResource(resources, R.drawable.tutor)
                var resizeBitmap: Bitmap =  Bitmap.createScaledBitmap(bitmapDefault, 140, 140, false)
*/
                makeUserMarkerCurrentLocation(mGoogleMap, currentLocation)
                userMarker = mGoogleMap.addMarker(MarkerOptions().position(currentLocation).title("marker")) //.icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap))
                userMarker.setTag(personalData)
            }

            override fun onCancelled(error: DatabaseError) {
            // Failed to read
            }
        })
    }

    private fun makeStudyMarkerCurrentLocation(googleMap: GoogleMap, currentLocation: LatLng){
//        if (checkStudyMarker == 0){
//            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 2f))
//        }
        if(checkStudyMarker > 0){
            studyMarker.remove()
        }
        checkStudyMarker = 1
    }

    private fun makeUserMarkerCurrentLocation(googleMap: GoogleMap, currentLocation: LatLng){
        if (checkMarker == 0){
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 2f))
        }
        if(checkMarker > 0){
            userMarker.remove()
        }
        checkMarker = 1
    }

    private fun makeInfoWindowGoogleMap(googleMap: GoogleMap){
        googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {

            override fun getInfoWindow(marker: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {

                var dataTutor : Member = marker.getTag() as Member
                val v = layoutInflater.inflate(R.layout.info_window, null)
//                var imageProfile = v.findViewById<View>(R.id.imgProfie) as ImageView
                val fName = v.findViewById(R.id.name) as TextView
                val lName = v.findViewById(R.id.lastname) as TextView
                val phone = v.findViewById(R.id.phone) as TextView

//                var photoRef = storageReference.child("photo/"+dataTutor.id)
//                var localFile = File.createTempFile("images", "jpg")

                fName.text = "FirstName : " + dataTutor.name
                lName.text = "LastName : " + dataTutor.lastname
                phone.text = "Phone : " + dataTutor.phone

                return v
            }
        })
    }

    override fun onInfoWindowClick(marker: Marker) {
        var dataTutor : Member = marker.getTag() as Member
        var alreadyRequest : Int = 0
//        Toast.makeText(this.activity ,dataX.id.toString() + dataX.msg,   Toast.LENGTH_SHORT).show()

        var detailDialog = AlertDialog.Builder(this.activity!!).create()
        val view = layoutInflater.inflate(R.layout.dialog_info_googlemap, null) as View
        detailDialog.setView(view)

        val email = view.findViewById<View>(R.id.email) as TextView
        val fName = view.findViewById<View>(R.id.name) as TextView
        val lName = view.findViewById<View>(R.id.lastName) as TextView
        val phone = view.findViewById<View>(R.id.phone) as TextView
        val status = view.findViewById<View>(R.id.status) as TextView
        val subject = view.findViewById<View>(R.id.subject) as TextView

        val requestBtn = view.findViewById<View>(R.id.requestBtn) as Button
        val cancelBtn = view.findViewById<View>(R.id.cancelBtn) as Button

        email.text = "email : "+ dataTutor.email
        fName.text = "FirstName : "+ dataTutor.name
        lName.text = "LastName : "+ dataTutor.lastname
        phone.text = "Phone : "+ dataTutor.phone
        status.text = "Status : "+ dataTutor.status
        subject.text = "Subject : "+ dataTutor.subject

//        database.child(dataTutor.id).child("request").addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val children = dataSnapshot!!.children
//                children.forEach{
//                    println(it.key)
//                    if(it.key.toString() == currentUserUid){
//                        println("==========================already request")
//                        requestBtn.isEnabled = false
//                        Toast.makeText(activity, "This tutor is already request", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read
//            }
//        })
//
//        requestBtn.setOnClickListener {
//            requestBtn.isEnabled = false
//            Toast.makeText(activity, "Request sended successful", Toast.LENGTH_SHORT).show()
//        }

        cancelBtn.setOnClickListener{
            detailDialog.dismiss()
        }
        detailDialog.show()
    }

}
