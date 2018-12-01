package com.egco428.egco428project.Fragments

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment

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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class MapFragment: Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, SensorEventListener {

    private var rootView: View? = null
    private var ERROR_DIALOG_REQUEST:Int = 9001
    lateinit var mapFragment: SupportMapFragment
    lateinit var dataPersonal: Member
    lateinit var database: DatabaseReference
    lateinit var currentUserUid: String

    lateinit var storageReference: StorageReference
    lateinit var storage: FirebaseStorage

    private var sensorManager: SensorManager? = null
    private var lastUpdate: Long = 0
    private var toastShake: Toast? = null
    private var toastShowing: Toast? = null

    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

//    private var locationManager: LocationManager? = null
//    private var locationListener: LocationListener? = null
//    private var currentUserLocation: LatLng = LatLng(0.0, 0.0)

    lateinit var mGoogleMap: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_map, container, false)
        toastShake = Toast.makeText(this.activity,"Shake to find tutor",Toast.LENGTH_SHORT)
        toastShowing = Toast.makeText(this.activity, "Showing tutor", Toast.LENGTH_SHORT)

        if (isServicesOK()){
            Toast.makeText(this.activity,"Service Working", Toast.LENGTH_SHORT).show()

//            locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//            locationListener = object : LocationListener{
//
//                override fun onLocationChanged(location: Location?) {
//                    currentUserLocation = LatLng(location!!.latitude, location!!.longitude)
//                }
//
//                override fun onProviderDisabled(p0: String?) {
//
//                }
//
//                override fun onProviderEnabled(p0: String?) {
//                }
//
//                override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
//                }
//
//            }
//            requestLocation()

            toastShake!!.show()

            sensorManager = activity!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            lastUpdate = System.currentTimeMillis()

            database = FirebaseDatabase.getInstance().getReference("Members")

            storage = FirebaseStorage.getInstance()
            storageReference = storage.reference

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
//        googleMap.addMarker(MarkerOptions().position(currentUserLocation).title("You are here")).showInfoWindow()
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUserLocation, 6f))

        mGoogleMap = googleMap
        googleMap.setOnInfoWindowClickListener(this)
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
            addMarkerFromFireBase(mGoogleMap)
            makeInfoWindowGoogleMap(mGoogleMap)
            toastShake!!.cancel()
            toastShowing!!.show()
            sensorManager!!.unregisterListener(this)
        }
    }

    private fun addMarkerFromFireBase(googleMap: GoogleMap){
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val children = dataSnapshot!!.children
                children.forEach{
                    if(it.child("statusOnOff").value.toString() == "on" && it.child("status").value.toString() == "tutor"){
                        dataPersonal = Member(
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
                                it.child("credit").value.toString())

                        println("========================================================")
                        println(dataPersonal.latitude  + dataPersonal.longitude)
                        println(dataPersonal.name)

                        var photoRef = storageReference!!.child("photo/"+dataPersonal.id)
                        var localFile = File.createTempFile("images", "jpg")
                        var bitmapDefault = BitmapFactory.decodeResource(resources, R.drawable.tutor)
                        var resizeBitmap: Bitmap =  Bitmap.createScaledBitmap(bitmapDefault, 140, 140, false)

                        photoRef.getFile(localFile).addOnSuccessListener{

                            var uri = Uri.fromFile(localFile)
                            var bitmapPerson = MediaStore.Images.Media.getBitmap(activity!!.contentResolver,uri)
                            resizeBitmap =  Bitmap.createScaledBitmap(bitmapPerson, 140, 140, false)

                        }

                        googleMap.addMarker(MarkerOptions().position(LatLng(dataPersonal.latitude.toDouble(), dataPersonal.longitude.toDouble())).title("marker").icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap))).setTag(dataPersonal)
//                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(dataPersonal.latitude.toDouble(), dataPersonal.longitude.toDouble()), 2F))
                    }
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

                var dataPersonal : Member = marker.getTag() as Member
                val v = layoutInflater.inflate(R.layout.info_window, null)
//                var imageProfile = v.findViewById<View>(R.id.imgProfie) as ImageView
                val fName = v.findViewById(R.id.name) as TextView
                val lName = v.findViewById(R.id.lastname) as TextView
                val phone = v.findViewById(R.id.phone) as TextView

                var photoRef = storageReference.child("photo/"+dataPersonal.id)
                var localFile = File.createTempFile("images", "jpg")

                fName.text = "FirstName : " + dataPersonal.name
                lName.text = "LastName : " + dataPersonal.lastname
                phone.text = "Phone : " + dataPersonal.phone

                return v
            }
        })
    }

    override fun onInfoWindowClick(marker: Marker) {
        var personData : Member = marker.getTag() as Member
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

        email.text = "email : "+ personData.email
        fName.text = "FirstName : "+ personData.name
        lName.text = "LastName : "+ personData.lastname
        phone.text = "Phone : "+ personData.phone
        status.text = "Status : "+ personData.status
//        subject.text = "Subject : "+ personData.s

        requestBtn.setOnClickListener {
            Toast.makeText(this.activity, "Request sended successful", Toast.LENGTH_SHORT).show()
            saveRequest(personData)
            requestBtn.isEnabled = false

//            detailDialog.dismiss()
        }

        cancelBtn.setOnClickListener{
            detailDialog.dismiss()
        }
        detailDialog.show()
    }

    private fun saveRequest(personalData : Member){
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            currentUserUid = user.uid
            database.child(currentUserUid).child("request").child(currentUserUid).child("name").setValue(personalData.name)
            database.child(currentUserUid).child("request").child(currentUserUid).child("lastname").setValue(personalData.lastname)
            database.child(currentUserUid).child("request").child(currentUserUid).child("phone").setValue(personalData.phone)
//            database.child(currentUserUid).child("request").child(currentUserUid).setValue(personalData.)
        }



//        Name = name.text.toString()
//        database.child(uid).child("name").setValue(Name)
//        var reqeustDatabase :DatabaseReference = FirebaseDatabase.getInstance().getReference("Members")
//        database.child(personalData.id).child("request").child(personalData.id).setValue(personalData.name)
//        database.child(personalData.id).child("request").child(personalData.id).setValue(personalData.lastname)
//        database.child(personalData.id).child("request").child(personalData.id).setValue(personalData.phone)
//        database.child(personalData.id).child("request").child(personalData.id).setValue(personalData.)


    }
//    private fun requestLocation(){
//        if(ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED){
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET),10)
//            }
//            return
//        }
//        locationManager!!.requestSingleUpdate("gps",locationListener,null)
//
//    }

    override fun onPause() {
        super.onPause()
        Toast.makeText(this.activity, "Sensor stop working", Toast.LENGTH_SHORT).show()
        sensorManager!!.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(this.activity, "Sensor start working again", Toast.LENGTH_SHORT).show()
        sensorManager!!.registerListener(this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
    }
}
