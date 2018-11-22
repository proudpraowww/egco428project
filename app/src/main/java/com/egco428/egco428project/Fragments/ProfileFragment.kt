package com.egco428.egco428project.Fragments

import android.app.Activity
import android.content.ContentResolver
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
import android.provider.MediaStore
import android.widget.Toast
import com.egco428.egco428project.Activities.SigninActivity
import com.egco428.egco428project.Model.Member
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.history_dialog.*
import kotlinx.android.synthetic.main.photo_edit_dialog.*
import org.w3c.dom.Text
import java.io.IOException


class ProfileFragment: Fragment(), View.OnClickListener {


    private var mAuth: FirebaseAuth? = null
    lateinit var database: DatabaseReference
    lateinit var currentEmail: String
    lateinit var uid: String
    lateinit var password: String
    lateinit var id: String
    lateinit var Name:String
    lateinit var Lastname: String

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
    private var logoutBtn: Button? = null
    private val IMAGE_REQUEST = 1234
    private var filePath: Uri? = null
    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false)

        historyBtn = rootView!!.findViewById(R.id.historyBtn) as ImageButton
        paymentBtn = rootView!!.findViewById(R.id.paymentBtn) as ImageButton
        creditText = rootView!!.findViewById(R.id.creditText) as TextView
        editBtn = rootView!!.findViewById(R.id.editBtn) as ImageButton
        studentPhoto = rootView!!.findViewById(R.id.student_profile_photo) as ImageButton
        emailText = rootView!!.findViewById(R.id.emailText) as TextView
        nameText = rootView!!.findViewById(R.id.user_profile_name) as TextView
        telText = rootView!!.findViewById(R.id.telText) as TextView
        schoolText = rootView!!.findViewById(R.id.schoolText) as TextView
        logoutBtn = rootView!!.findViewById(R.id.logoutBtn) as Button

        historyBtn!!.setOnClickListener(this)
        paymentBtn!!.setOnClickListener(this)
        editBtn!!.setOnClickListener(this)
        studentPhoto!!.setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Members")

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            currentEmail = user.email.toString()
            uid = user.uid

            emailText!!.text = "E-mail : " + currentEmail.toString()
        }

        logoutBtn!!.setOnClickListener {
            logoutKeng()
        }

        database.addValueEventListener(object : ValueEventListener {
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
                            "Credit : 0"
                        }else{
                            creditText!!.text = "Credit : " + it.child("credit").value.toString()
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


    private fun logoutKeng(){
        mAuth!!.signOut()
        val intent = Intent(this.activity,SigninActivity::class.java)
        startActivity(intent)
    }


    override fun onClick(v: View?) {
        if(v === historyBtn){

            val names = arrayOf("A", "B", "C", "D")
            var alertDialog = AlertDialog.Builder(this.activity!!).create()
            val inflater = layoutInflater
            val convertView = inflater.inflate(R.layout.history_dialog, null) as View
            alertDialog.setView(convertView)
            val lv = convertView.findViewById<View>(R.id.historyList) as ListView
            val adapter = ArrayAdapter(this.activity!!, android.R.layout.simple_list_item_1, names)
            lv.setAdapter(adapter)
            val btn = convertView.findViewById<View>(R.id.backBtn) as Button
            btn.setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog.show()
        }
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

            submit.setOnClickListener {
                var values = 0
                if(check100.isChecked){values = values+100}
                if(check200.isChecked){values = values+200}
                if(check300.isChecked){values = values+300}
                if(check400.isChecked){values = values+400}
                if(check500.isChecked){values = values+500}
                if(check600.isChecked){values = values+600}
                creditText!!.text = "Credit : "+values
                database.child(uid).child("credit").setValue(values)


                alertDialog.dismiss()
            }

            cancel.setOnClickListener{
                alertDialog.dismiss()
            }
            alertDialog.show()
        }
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
            cancel.setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog.show()
        }
        else if(v===studentPhoto){

            var alertDialog = AlertDialog.Builder(this.activity!!).create()
            val inflater = layoutInflater
            val convertView = inflater.inflate(R.layout.photo_edit_dialog, null) as View
            alertDialog.setView(convertView)
            var gallery = convertView.findViewById<View>(R.id.album) as TextView
            var camera = convertView.findViewById<View>(R.id.camera) as TextView
            var cancel = convertView.findViewById<View>(R.id.cancel) as TextView

            gallery.setOnClickListener {
                selectPhoto()
                alertDialog.dismiss()

            }
            camera.setOnClickListener {
                takePhoto()
                alertDialog.dismiss()

            }
            cancel.setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog.show()



        }

    }

    private fun selectPhoto(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null){
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.activity!!.contentResolver,filePath)
                studentPhoto!!.setImageBitmap(bitmap)
            }catch (e: IOException){
                e.printStackTrace()
            }
        }

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            val extras = data!!.extras
            val photo = extras!!.get("data") as Bitmap

            studentPhoto!!.setImageBitmap(photo)

        }

    }

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

}