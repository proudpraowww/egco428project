package com.egco428.egco428project.Fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.egco428.egco428project.R
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.egco428.egco428project.Activities.SigninActivity
import com.egco428.egco428project.Model.Member
import com.egco428.egco428project.Model.history
import com.egco428.egco428project.historyAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.photo_edit_dialog.*
import java.io.IOException
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class ProfileTutorFragment: Fragment(), View.OnClickListener {


//=================================declare variable===========================================

    private var mAuth: FirebaseAuth? = null
    lateinit var database: DatabaseReference
    lateinit var currentEmail: String
    lateinit var uid: String
    lateinit var password: String
    lateinit var id: String
    lateinit var Name:String
    lateinit var Lastname: String
    lateinit var Credit: String
    lateinit var historyData: MutableList<history>

    private var rootView: View? = null
    private var historyBtn: ImageButton? = null
    private var creditText: TextView? = null
    private var editBtn: ImageButton? = null
    private var tutorPhoto: ImageButton? = null
    private var emailText: TextView? = null
    private var nameText: TextView? = null
    private var telText: TextView? = null
    private var courseText: TextView? = null
    private var priceText: TextView? = null
    private var logoutBtn: ImageButton? = null


    private val IMAGE_REQUEST = 1234
    private var filePath: Uri? = null
    private val REQUEST_IMAGE_CAPTURE = 1
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_tutor_profile, container, false)


//===================================display loading dialog===============================

        var loadingDialog = AlertDialog.Builder(this.activity!!).create()
        val inflater = layoutInflater
        val convertView = inflater.inflate(R.layout.loading_dialog, null) as View
        loadingDialog.setView(convertView)
        loadingDialog.show()

        Handler().postDelayed({
            //doSomethingHere()
            loadingDialog.dismiss()

        }, 3000)


//=====================================declare xml layout===================================

        historyBtn = rootView!!.findViewById(R.id.historyBtn) as ImageButton
        creditText = rootView!!.findViewById(R.id.creditText) as TextView
        editBtn = rootView!!.findViewById(R.id.editBtn) as ImageButton
        tutorPhoto = rootView!!.findViewById(R.id.tutor_profile_photo) as ImageButton
        emailText = rootView!!.findViewById(R.id.emailText) as TextView
        nameText = rootView!!.findViewById(R.id.user_profile_name) as TextView
        telText = rootView!!.findViewById(R.id.telText) as TextView
        courseText = rootView!!.findViewById(R.id.courseText) as TextView
        priceText = rootView!!.findViewById(R.id.priceText) as TextView
        logoutBtn = rootView!!.findViewById(R.id.logoutBtn) as ImageButton
        val gpsSwitch = rootView!!.findViewById(R.id.gpsSwitch) as Switch


// ===================================set button onclickListener===========================

        historyBtn!!.setOnClickListener(this)
        editBtn!!.setOnClickListener(this)
        tutorPhoto!!.setOnClickListener(this)
        logoutBtn!!.setOnClickListener {
            logout()
        }

//=============================get firebase database and firebase storage===============================================

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Members")
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference


// ============================get email and id from firebase authentication================

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            currentEmail = user.email.toString()
            uid = user.uid

            emailText!!.text = "E-mail : " + currentEmail.toString()
        }


//======================get image from firebase storage and display in imageView================================

        val photoRef = storageReference!!.child("photo/"+uid)

        val localFile = File.createTempFile("images", "jpg")
        photoRef.getFile(localFile)
                .addOnSuccessListener(OnSuccessListener<Any> {
                    val uri = Uri.fromFile(localFile)
                    val bitmap = MediaStore.Images.Media.getBitmap(this.activity!!.contentResolver,uri)
                    tutorPhoto!!.setImageBitmap(bitmap)

                }).addOnFailureListener(OnFailureListener {
                })


//==============================get user information from firebase========================================

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val children = dataSnapshot!!.children
                val msgList:ArrayList<Member>? = null

                children.forEach{
                    if(it.child("email").value.toString().equals(currentEmail)){
                        Name = it.child("name").value.toString()
                        Lastname = it.child("lastname").value.toString()
                        var nameAndLastname = Name + " " + Lastname
                        nameText!!.text = nameAndLastname
                        //test
                        Log.d("TutorActivity", it.child("status").value.toString())
                        telText!!.text = "Tel : " + it.child("phone").value.toString()
                        courseText!!.text = "Subject : " + it.child("subject").value.toString()
                        if(it.child("credit").value.toString().isEmpty()){
                            creditText!!.text = "Credit : 0"
                            Credit = "0".toString()
                        }else{
                            creditText!!.text = "Credit : " + it.child("credit").value.toString()
                            Credit = it.child("credit").value.toString()
                        }
                        if(it.child("statusOnOff").value.toString() == "on") {
                            gpsSwitch.isChecked = true
                        }else{
                            database.child(uid).child("statusOnOff").setValue("off")
                            gpsSwitch.isChecked = false
                        }
                        priceText!!.text = "Course price : " + it.child("course_price").value.toString()
                        password = it.child("password").value.toString()
                        id = it.child("id").value.toString()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read
            }
        })


//===============================save location to database when changed=====================================

        locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {

            override fun onLocationChanged(p0: Location?) {

                database.child(uid).child("latitude").setValue(p0!!.latitude)
                database.child(uid).child("longitude").setValue(p0!!.longitude)
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


//==============================get/stop current location when switch turn on/off=============================

        gpsSwitch.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                database.child(uid).child("statusOnOff").setValue("on")
                requestLocation()

            } else {
                database.child(uid).child("statusOnOff").setValue("off")
                endRequestLocation()

            }
        }
        return rootView
    }

//=========================log out function===================================

    private fun logout(){
        database.child(uid).child("statusOnOff").setValue("off")
        mAuth!!.signOut()
        val intent = Intent(this.activity,SigninActivity::class.java)
        startActivity(intent)
    }

//========================onclick listener method================================
    override fun onClick(v: View?) {

// ========================= display history dialog================================
        if(v === historyBtn){

            var alertDialog = AlertDialog.Builder(this.activity!!).create()
            val inflater = layoutInflater
            val convertView = inflater.inflate(R.layout.history_dialog, null) as View
            alertDialog.setView(convertView)
            val lv = convertView.findViewById<View>(R.id.historyList) as ListView
            val noHistory = convertView.findViewById<View>(R.id.noHistory) as TextView
            historyData = mutableListOf()

            //get history data from firebase and show in listView
            database.child(uid).child("history").addValueEventListener(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError?) {

                }
                override fun onDataChange(p0: DataSnapshot?) {
                    if (p0!!.exists()){
                        historyData.clear()
                        for (i in p0.children){
                            val message = i.getValue(history::class.java)
                            historyData.add(message!!)
                        }
                        val adapter = historyAdapter(context!!, R.layout.history, historyData,"Student")
                        lv.adapter = adapter
                    }
                    else{
                        noHistory.visibility = View.VISIBLE
                    }
                }
            })
            // close dialog
            val btn = convertView.findViewById<View>(R.id.backBtn) as Button
            btn.setOnClickListener {
                alertDialog.dismiss()
            }


            alertDialog.show()
        }

// ========================= display edit dialog================================
        else if(v === editBtn){

            var alertDialog = AlertDialog.Builder(this.activity!!).create()
            val inflater = layoutInflater
            val convertView = inflater.inflate(R.layout.tutor_edit_dialog, null) as View
            alertDialog.setView(convertView)
            var name = convertView.findViewById<View>(R.id.editName) as EditText
            var surename = convertView.findViewById<View>(R.id.editSurename) as EditText
            var course = convertView.findViewById<View>(R.id.editCourse) as EditText
            var price = convertView.findViewById<View>(R.id.editPrice) as EditText
            var tel = convertView.findViewById<View>(R.id.editTel) as EditText
            val save = convertView.findViewById<View>(R.id.saveBtn) as Button
            val cancel = convertView.findViewById<View>(R.id.abolishBtn) as Button

            //get new data display in textView and save to firebase
            save.setOnClickListener {

                if(name.text.isNotEmpty()){
                    Name = name.text.toString()
                    database.child(uid).child("name").setValue(Name)
                }
                if(surename.text.isNotEmpty()){
                    Lastname = surename.text.toString()
                    database.child(uid).child("lastname").setValue(Lastname)
                }
                if(course.text.isNotEmpty()){
                    courseText!!.text = course.text.toString()
                    database.child(uid).child("subject").setValue(course.text.toString())

                }
                if(tel.text.isNotEmpty()){
                    telText!!.text = tel.text.toString()
                    database.child(uid).child("phone").setValue(tel.text.toString())
                }
                if(price.text.isNotEmpty()){
                    priceText!!.text = price.text.toString()
                    database.child(uid).child("course_price").setValue(price.text.toString())
                }

                val nameLastname = Name + " " + Lastname
                nameText!!.text = nameLastname
                alertDialog.dismiss()
            }
            //close dialog
            cancel.setOnClickListener {
                alertDialog.dismiss()
            }


            alertDialog.show()
        }

//=============================get photo dialog=========================================

        else if(v===tutorPhoto){

            var alertDialog = AlertDialog.Builder(this.activity!!).create()
            val inflater = layoutInflater
            val convertView = inflater.inflate(R.layout.photo_edit_dialog, null) as View
            alertDialog.setView(convertView)
            var gallery = convertView.findViewById<View>(R.id.album) as TextView
            var camera = convertView.findViewById<View>(R.id.camera) as TextView
            var cancel = convertView.findViewById<View>(R.id.cancel) as TextView

            // choose photo from gallery
            gallery.setOnClickListener {
                selectPhoto()
                alertDialog.dismiss()

            }

            // take a photo
            camera.setOnClickListener {
                takePhoto()
                alertDialog.dismiss()

            }

            // close dialog
            cancel.setOnClickListener {
                alertDialog.dismiss()
            }


            alertDialog.show()
        }
    }

// ====================================get photo from gallery function=================================

    private fun selectPhoto(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_REQUEST)
    }

// =====================================when receive photo function===========================================

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //display photo from gallery
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null){
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.activity!!.contentResolver,filePath)
                tutorPhoto!!.setImageBitmap(bitmap)
                uploadFile()
            }catch (e: IOException){
                e.printStackTrace()
            }
        }

        //display photo from camera and save photo to firebase storage
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            val extras = data!!.extras
            val photo = extras!!.get("data") as Bitmap
            tutorPhoto!!.setImageBitmap(photo)

            var baos:ByteArrayOutputStream = ByteArrayOutputStream(8192)
            photo.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            var data = baos.toByteArray()
            val imageRef = storageReference!!.child("photo/"+uid)
            imageRef.putBytes(data)
        }

    }

//=======================take a photo function===================================
    private fun takePhoto(){
        launchCamera()
        if(!hasCamera()){
            camera.isEnabled = false
        }
    }

    private fun hasCamera() : Boolean{
        return this.activity!!.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

    }

    fun launchCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent,REQUEST_IMAGE_CAPTURE)
    }


//=========================save photo from gallery to firebase storage=============================

    private fun uploadFile(){
        if(filePath !== null){
            val imageRef = storageReference!!.child("photo/"+uid)
            imageRef.putFile(filePath!!)
        }

    }


//=============================== request location function========================================
    private fun requestLocation(){
        if(ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET),10)
            }
            return
        }
        locationManager!!.requestLocationUpdates("gps",1000,0f,locationListener)
        //locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,0f,locationListener)

    }

//=============================stop request location function========================================

    private fun endRequestLocation(){
        locationManager!!.removeUpdates(locationListener)
    }

}