package com.egco428.egco428project.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.percent.PercentRelativeLayout
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.egco428.egco428project.Model.Member
import com.egco428.egco428project.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup_student.*
import kotlinx.android.synthetic.main.activity_signup_teacher.*
import kotlinx.android.synthetic.main.activity_type_signin.*
import kotlinx.android.synthetic.main.activity_type_signup.*


class SignupActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    lateinit var database: DatabaseReference
    private var isSigninScreen: Boolean? = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Members")

        //Go to Signup of student
        tvSigninInvoker2.setOnClickListener {
            isSigninScreen = true
            showSignupStudentForm()
        }

        //Go to Signup of teacher
        tvSignupInvoker2.setOnClickListener {
            isSigninScreen = false
            showSignupTeacherForm()
        }

        //Signup Student
        btnSignupStudent.setOnClickListener{
            checkStudentEmailexits()
        }

        //Signup Teacher
        btnSignupTeacher.setOnClickListener{
            checkTeacherEmailexits()
        }

    }

    //Go to type of techer
    private fun showSignupTeacherForm() {
        val paramsLogin = llSignin2.getLayoutParams() as PercentRelativeLayout.LayoutParams
        val infoLogin = paramsLogin.percentLayoutInfo
        infoLogin.widthPercent = 0.15f
        llSignin2.requestLayout()


        val paramsSignup = llSignup2.getLayoutParams() as PercentRelativeLayout.LayoutParams
        val infoSignup = paramsSignup.percentLayoutInfo
        infoSignup.widthPercent = 0.85f
        llSignup2.requestLayout()

        tvSignupInvoker2.setVisibility(View.GONE)
        tvSigninInvoker2.setVisibility(View.VISIBLE)
        val translate = AnimationUtils.loadAnimation(applicationContext, R.anim.translate_right_to_left)
        llSignup2.startAnimation(translate)

    }

    //Go to type of student
    private fun showSignupStudentForm() {
        val paramsLogin = llSignin2.getLayoutParams() as PercentRelativeLayout.LayoutParams
        val infoLogin = paramsLogin.percentLayoutInfo
        infoLogin.widthPercent = 0.85f
        llSignin2.requestLayout()

        val paramsSignup = llSignup2.getLayoutParams() as PercentRelativeLayout.LayoutParams
        val infoSignup = paramsSignup.percentLayoutInfo
        infoSignup.widthPercent = 0.15f
        llSignup2.requestLayout()

        tvSignupInvoker2.setVisibility(View.VISIBLE)
        tvSigninInvoker2.setVisibility(View.GONE)
        val translate = AnimationUtils.loadAnimation(applicationContext, R.anim.translate_left_to_right)
        llSignin2.startAnimation(translate)
    }

    //Register ID for student
    private fun checkStudentEmailexits(){

        var email = emailStudentEdittext.text.toString()
        var password = passwordStudentEdittext.text.toString()
        var name = nameStudentEdittext.text.toString()
        var lastname = lastnameStudentEdittext.text.toString()
        var school = schoolEdittext.text.toString()
        var phone = phoneStudentEdittext.text.toString()
        //val user = FirebaseAuth.getInstance().currentUser

        if(!email.isEmpty() && !password.isEmpty() && !name.isEmpty() && !lastname.isEmpty() && !phone.isEmpty()
                && password.length >= 6  && !school.isEmpty()) {
            mAuth!!.fetchProvidersForEmail(emailStudentEdittext.text.toString())
                    .addOnCompleteListener(){
                        var check = !it.getResult().providers!!.isEmpty()
                        if(!check){

                            //Create Email in Firebase Authentication
                            mAuth!!.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener{
                                        //val messageId = database.push().key
                                        val user = FirebaseAuth.getInstance().currentUser
                                        val messageData = Member(user!!.uid,email, password, name,lastname,"student",phone,school,"","","","","","","")
                                        database.child(user!!.uid).setValue(messageData).addOnCompleteListener({
                                            Toast.makeText(applicationContext,"Completely",Toast.LENGTH_SHORT).show()
                                        })

                                        //After you registerd then you'll go to the MainActivity
                                        mAuth!!.signInWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(this) { task ->
                                                    if (task.isSuccessful) {
                                                        val user = mAuth!!.getCurrentUser()
                                                        Toast.makeText(applicationContext,"Sigin Success Fully....",Toast.LENGTH_SHORT).show()
                                                        val intent = Intent(this, MainActivity::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    } else {
                                                        // If sign in fails, display a message to the user.
                                                        Log.d("Signin","Error Failed")
                                                        Toast.makeText(applicationContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                    }
                        } else{
                            Toast.makeText(this,"Email already present", Toast.LENGTH_SHORT).show()
                        }
                    }
        }else{
            Toast.makeText(this,"Please enter information", Toast.LENGTH_SHORT).show()
        }
    }

    //Register ID for Teacher
    private fun checkTeacherEmailexits(){

        var email = emailTeacherEdittext.text.toString()
        var password = passwordTeacherEdittext.text.toString()
        var name = nameTeacherEdittext.text.toString()
        var lastname = lastnameTeacherEdittext.text.toString()
        var phone = phoneTeacherEdittext.text.toString()
        var subject = subjectTeacherEdittext.text.toString()
        var course_price = coursePriceTeacherEdittext.text.toString()

        //val user = FirebaseAuth.getInstance().currentUser

        if(!email.isEmpty() && !password.isEmpty() && !name.isEmpty() && !lastname.isEmpty() && !phone.isEmpty()
                && password.length >= 6 && !subject.isEmpty() && !course_price.isEmpty()) {
            mAuth!!.fetchProvidersForEmail(emailTeacherEdittext.text.toString())
                    .addOnCompleteListener(){
                        var check = !it.getResult().providers!!.isEmpty()
                        if(!check){

                            //Create Email in Firebase Authentication
                            mAuth!!.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener{
                                        //val messageId = database.push().key
                                        val user = FirebaseAuth.getInstance().currentUser
                                        val messageData = Member(user!!.uid,email, password, name,lastname,"tutor",phone,"","","","","",subject,course_price,"")
                                        database.child(user!!.uid).setValue(messageData).addOnCompleteListener({
                                            Toast.makeText(applicationContext,"Completely",Toast.LENGTH_SHORT).show()
                                        })

                                        //After you registerd then you'll go to the TutorActivity
                                        mAuth!!.signInWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(this) { task ->
                                                    if (task.isSuccessful) {
                                                        val user = mAuth!!.getCurrentUser()
                                                        Toast.makeText(applicationContext,"Sigin Success Fully....",Toast.LENGTH_SHORT).show()
                                                        val intent = Intent(this, TutorActivity::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    } else {
                                                        // If sign in fails, display a message to the user.
                                                        Log.d("Signin","Error Failed")
                                                        Toast.makeText(applicationContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                    }
                        } else{
                            Log.d("Register","Error Failed")
                            Toast.makeText(this,"Email already present", Toast.LENGTH_SHORT).show()
                        }
                    }
        }else{
            Toast.makeText(this,"Please enter information", Toast.LENGTH_SHORT).show()
        }
    }

}