package com.egco428.egco428project.Fragments

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.getSystemService

import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.egco428.egco428project.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.GoogleMap


import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.database.*
import com.egco428.egco428project.Model.Member
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class MapFragment: Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, SensorEventListener {

    private var rootView: View? = null
    private var ERROR_DIALOG_REQUEST:Int = 9001
    lateinit var dataTutor: Member
    lateinit var currentUserUid: String
    lateinit var mapFragment: SupportMapFragment

    lateinit var database: DatabaseReference

    lateinit var storageReference: StorageReference
    lateinit var storage: FirebaseStorage

    private var sensorManager: SensorManager? = null
    private var lastUpdate: Long = 0
    private var toastShake: Toast? = null

    lateinit var locationManager: LocationManager
    lateinit var locationListener: LocationListener
    private  var userCurrentLocation: LatLng = LatLng(0.0, 0.0)

    lateinit var mGoogleMap: GoogleMap
    lateinit var userMarker: Marker
    private var checkMarker: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_map, container, false)
        toastShake = Toast.makeText(this.activity,"Shake to find tutor",Toast.LENGTH_SHORT)

        if (isServicesOK()){
//            Toast.makeText(this.activity,"Service Working", Toast.LENGTH_SHORT).show()
            println("Service Working")

            toastShake!!.show()

            sensorManager = activity!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            lastUpdate = System.currentTimeMillis()

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

            locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationListener = object: LocationListener{
                override fun onLocationChanged(location: Location?) {
                    userCurrentLocation = LatLng(location!!.latitude, location!!.longitude)
                    println(location!!.latitude)
                    println(location!!.longitude)
                    println("==========================================")
                    println(userCurrentLocation.latitude)
                    println(userCurrentLocation.longitude)
                    makeUserMarkerCurrentLocation(mGoogleMap)
                }

                override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
                }

                override fun onProviderDisabled(p0: String?) {
                }

                override fun onProviderEnabled(p0: String?) {
                    val i = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(i)
                }
            }
            requestLocation()

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            10 -> requestLocation()
            else -> {}
        }
    }

    private fun requestLocation(){
        if(ActivityCompat.checkSelfPermission(this.context!!,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.context!!,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.INTERNET),10)
            }

            return
        }
        //use this if run on real mobile
//        locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000,0f,locationListener)
        //use this if run on emulator
        locationManager!!.requestLocationUpdates("gps",5000,0f,locationListener)

//            locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0f,locationListener)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        println("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii")
        println(userCurrentLocation.latitude)
        println(userCurrentLocation.longitude)
        mGoogleMap = googleMap
//        userMarker = googleMap
        googleMap.setOnInfoWindowClickListener(this)
    }

    private fun makeUserMarkerCurrentLocation(googleMap: GoogleMap){
        if (checkMarker == 0){
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userCurrentLocation, 10f))
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

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER){
            getAccelerometer(event)
        }
    }

    private fun getAccelerometer(event: SensorEvent?){
        val values = event!!.values
        val x = values[0]
        val y = values[1]
        val z = values[2]

        val accel= (x*x+y*y+z*z)/(SensorManager.GRAVITY_EARTH*SensorManager.GRAVITY_EARTH)
        val actualTime = System.currentTimeMillis()

        if (accel>=2){
            if (actualTime-lastUpdate < 200){
                return
            }
            Toast.makeText(this.activity, "Showing tutor", Toast.LENGTH_SHORT).show()
            addMarkerFromFireBase(mGoogleMap)
            makeInfoWindowGoogleMap(mGoogleMap)
            toastShake!!.cancel()
//            sensorManager!!.unregisterListener(this)
        }
    }

    private fun addMarkerFromFireBase(googleMap: GoogleMap){
        var count = 0
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val children = dataSnapshot!!.children
                children.forEach{
                    if(it.child("statusOnOff").value.toString() == "on" && it.child("status").value.toString() == "tutor"){
                        count++
                        dataTutor = Member(
                                it.child("id").value.toString(),
                                it.child("email").value.toString(),
                                it.child("password").value.toString(),
                                it.child("name").value.toString(),
                                it.child("lastname").value.toString(),
                                it.child("status").value.toString(),
                                it.child("phone").value.toString(),
                                it.child("school").value.toString(),
                                it.child("statusOnOff").value.toString(),
                                it.child("latitude").value.toString(),
                                it.child("longitude").value.toString(),
                                it.child("credit").value.toString(),
                                it.child("subject").value.toString(),
                                it.child("course_price").value.toString(),
                                it.child("study_status").value.toString(),
                                it.child("start_time").value.toString())

                        println("========================================================")
                        println(dataTutor.latitude  + dataTutor.longitude)
                        println(dataTutor.name)

                        var photoRef = storageReference!!.child("photo/"+dataTutor.id)
                        var localFile = File.createTempFile("images", "jpg")
                        var bitmapDefault = BitmapFactory.decodeResource(resources, R.drawable.tutor)
                        var resizeBitmap: Bitmap =  Bitmap.createScaledBitmap(bitmapDefault, 140, 140, false)

//                        photoRef.getFile(localFile).addOnSuccessListener{
//
//                            var uri = Uri.fromFile(localFile)
//                            var bitmapPerson = MediaStore.Images.Media.getBitmap(activity!!.contentResolver,uri)
//                            resizeBitmap =  Bitmap.createScaledBitmap(bitmapPerson, 140, 140, false)
//                        }
                        googleMap.addMarker(MarkerOptions().position(LatLng(dataTutor.latitude.toDouble(), dataTutor.longitude.toDouble())).title("marker").icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap))).setTag(dataTutor)
//                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(personalData.latitude.toDouble(), personalData.longitude.toDouble()), 2F))
                    }
                }
                if (count == 0){
                    Toast.makeText(activity,"Sorry, There is no tutor", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read
            }
        })
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

                var photoRef = storageReference.child("photo/"+dataTutor.id)
                var localFile = File.createTempFile("images", "jpg")

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

        database.child(dataTutor.id).child("request").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val children = dataSnapshot!!.children
                children.forEach{
                    println(it.key)
                    if(it.key.toString() == currentUserUid){
                        println("==========================already request")
                        requestBtn.isEnabled = false
                        Toast.makeText(activity, "This tutor is already request", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read
            }
        })

        requestBtn.setOnClickListener {
            saveRequestToStudentUser(dataTutor)
            saveRequestToTutor(dataTutor)
            requestBtn.isEnabled = false
            Toast.makeText(activity, "Request sended successful", Toast.LENGTH_SHORT).show()
        }

        cancelBtn.setOnClickListener{
            detailDialog.dismiss()
        }
        detailDialog.show()
    }

    private fun saveRequestToStudentUser(dataTutor : Member){
            database.child(currentUserUid).child("request").child(dataTutor.id).child("name").setValue(dataTutor.name)
            database.child(currentUserUid).child("request").child(dataTutor.id).child("lastname").setValue(dataTutor.lastname)
            database.child(currentUserUid).child("request").child(dataTutor.id).child("phone").setValue(dataTutor.phone)
            database.child(currentUserUid).child("request").child(dataTutor.id).child("subject").setValue(dataTutor.subject)
            database.child(currentUserUid).child("request").child(dataTutor.id).child("response").setValue("")

    }
    private fun saveRequestToTutor(dataTutor : Member){
            database.child(currentUserUid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    database.child(dataTutor.id).child("request").child(currentUserUid).child("name").setValue(dataSnapshot.child("name").value)
                    database.child(dataTutor.id).child("request").child(currentUserUid).child("lastname").setValue(dataSnapshot.child("lastname").value)
                    database.child(dataTutor.id).child("request").child(currentUserUid).child("phone").setValue(dataSnapshot.child("phone").value)
                    database.child(dataTutor.id).child("request").child(currentUserUid).child("school").setValue(dataSnapshot.child("school").value)
                    database.child(dataTutor.id).child("request").child(currentUserUid).child("response").setValue("")
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read
                }
            })
    }

    override fun onPause() {
        super.onPause()
//        Toast.makeText(this.activity, "Sensor stop working", Toast.LENGTH_SHORT).show()
        sensorManager!!.unregisterListener(this)
        locationManager!!.removeUpdates(locationListener)
    }

    override fun onResume() {
        super.onResume()
//        Toast.makeText(this.activity, "Sensor start working again", Toast.LENGTH_SHORT).show()
        sensorManager!!.registerListener(this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
    }
}
