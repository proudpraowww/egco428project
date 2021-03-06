package com.egco428.egco428project.Fragments

import android.app.Activity
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
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
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


class ProfileFragment: Fragment(), View.OnClickListener {

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
    private var paymentBtn: ImageButton? = null
    private var creditText: TextView? = null
    private var editBtn: ImageButton? = null
    private var studentPhoto: ImageButton? = null
    private var emailText: TextView? = null
    private var nameText: TextView? = null
    private var telText: TextView? = null
    private var schoolText: TextView? = null
    private var logoutBtn: ImageButton? = null

    private val IMAGE_REQUEST = 1234
    private var filePath: Uri? = null
    private val REQUEST_IMAGE_CAPTURE = 1
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    private var databaseListener:ValueEventListener? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false)

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
        paymentBtn = rootView!!.findViewById(R.id.paymentBtn) as ImageButton
        creditText = rootView!!.findViewById(R.id.creditText) as TextView
        editBtn = rootView!!.findViewById(R.id.editBtn) as ImageButton
        studentPhoto = rootView!!.findViewById(R.id.student_profile_photo) as ImageButton
        emailText = rootView!!.findViewById(R.id.emailText) as TextView
        nameText = rootView!!.findViewById(R.id.user_profile_name) as TextView
        telText = rootView!!.findViewById(R.id.telText) as TextView
        schoolText = rootView!!.findViewById(R.id.schoolText) as TextView
        logoutBtn = rootView!!.findViewById(R.id.logoutBtn) as ImageButton


// ===================================set button onclickListener===========================

        historyBtn!!.setOnClickListener(this)
        paymentBtn!!.setOnClickListener(this)
        editBtn!!.setOnClickListener(this)
        studentPhoto!!.setOnClickListener(this)
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
                    studentPhoto!!.setImageBitmap(bitmap)

                }).addOnFailureListener(OnFailureListener {

                })


//==============================get user information from firebase========================================

        databaseListener = database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //val value = dataSnapshot.getValue(Member::class.java)
                val children = dataSnapshot!!.children
                val msgList:ArrayList<Member>? = null
//                member = Member(it.child("id").value.toString(), it.child("email").value.toString(), it.child("password").value.toString(), it.child("name").value.toString(), it.child("lastname").value.toString(), it.child("status").value.toString(), it.child("phone").value.toString())

                children.forEach{
                    if(it.child("email").value.toString().equals(currentEmail)){
                        Name = it.child("name").value.toString()
                        Lastname = it.child("lastname").value.toString()
                        var nameAndLastname = Name + " " + Lastname
                        nameText!!.text = nameAndLastname
                        telText!!.text = "Tel : " + it.child("phone").value.toString()
                        schoolText!!.text = "School : " + it.child("school").value.toString()
                        if(it.child("credit").value.toString().isEmpty()){
                            creditText!!.text = "Credit : 0"
                            Credit = "0".toString()
                        }else{
                            creditText!!.text = "Credit : " + it.child("credit").value.toString()
                            Credit = it.child("credit").value.toString()
                        }
                        password = it.child("password").value.toString()
                        id = it.child("id").value.toString()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read
            }
        })

        return rootView
    }


//=========================log out function===================================

    private fun logout(){
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
             database.child(uid).child("history").addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(p0: DataSnapshot?) {
                    if (p0!!.exists()){
                        historyData.clear()
                        for (i in p0.children){
                            val message = i.getValue(history::class.java)
                            historyData.add(message!!)
                        }
                        val adapter = historyAdapter(context!!, R.layout.history, historyData,"Tutor")
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

// ========================= display payment dialog================================
        else if(v === paymentBtn){

            var alertDialog = AlertDialog.Builder(this.activity!!).create()
            val inflater = layoutInflater
            val convertView = inflater.inflate(R.layout.payment_dialog, null) as View
            alertDialog.setView(convertView)
            val ten = convertView.findViewById<View>(R.id.cash_100) as ImageView
            val submit = convertView.findViewById<View>(R.id.submitBtn) as Button
            val cancel = convertView.findViewById<View>(R.id.cancelBtn) as Button
            val check100 = convertView.findViewById<View>(R.id.checkbox_cash100) as CheckBox
            val check200 = convertView.findViewById<View>(R.id.checkbox_cash200) as CheckBox
            val check300 = convertView.findViewById<View>(R.id.checkbox_cash300) as CheckBox
            val check400 = convertView.findViewById<View>(R.id.checkbox_cash400) as CheckBox
            val check500 = convertView.findViewById<View>(R.id.checkbox_cash500) as CheckBox
            val check600 = convertView.findViewById<View>(R.id.checkbox_cash600) as CheckBox

            //add credit to textView and save to firebase
            submit.setOnClickListener {
                var values = Credit.toInt()
                if(check100.isChecked){values = values+100}
                if(check200.isChecked){values = values+200}
                if(check300.isChecked){values = values+300}
                if(check400.isChecked){values = values+400}
                if(check500.isChecked){values = values+500}
                if(check600.isChecked){values = values+600}

                creditText!!.text = "Credit : "+ values.toString()
                database.child(uid).child("credit").setValue(values.toString())

                alertDialog.dismiss()
            }

            //close dialog
            cancel.setOnClickListener{
                alertDialog.dismiss()
            }


            alertDialog.show()
        }

// ========================= display edit dialog================================

        else if(v === editBtn){

            var alertDialog = AlertDialog.Builder(this.activity!!).create()
            val inflater = layoutInflater
            val convertView = inflater.inflate(R.layout.student_edit_dialog, null) as View
            alertDialog.setView(convertView)
            var name = convertView.findViewById<View>(R.id.editName) as EditText
            var surename = convertView.findViewById<View>(R.id.editSurename) as EditText
            var school = convertView.findViewById<View>(R.id.editSchool) as EditText
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
                if(school.text.isNotEmpty()){
                    schoolText!!.text = school.text.toString()
                    database.child(uid).child("school").setValue(school.text.toString())

                }
                if(tel.text.isNotEmpty()){
                    telText!!.text = tel.text.toString()
                    database.child(uid).child("phone").setValue(tel.text.toString())

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

        else if(v===studentPhoto){

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
                studentPhoto!!.setImageBitmap(bitmap)
                uploadFile()
            }catch (e: IOException){
                e.printStackTrace()
            }
        }

        //display photo from camera and save photo to firebase storage
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            val extras = data!!.extras
            val photo = extras!!.get("data") as Bitmap
            studentPhoto!!.setImageBitmap(photo)
            var baos: ByteArrayOutputStream = ByteArrayOutputStream(8192)
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

    //============================on Pause stop listening to firebase======================================

    override fun onPause() {
        super.onPause()
        database.removeEventListener(databaseListener)
    }

}