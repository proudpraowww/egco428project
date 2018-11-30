package com.egco428.egco428project.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.percent.PercentRelativeLayout
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.egco428.egco428project.Model.Member
import com.egco428.egco428project.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_signup.*


class SignupActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    lateinit var database: DatabaseReference
    private var isSigninScreen: Boolean? = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Members")

        tvSignupInvoker2.setOnClickListener {
            isSigninScreen = false
            showSignupTeacherForm()
        }
        tvSigninInvoker2.setOnClickListener {
            isSigninScreen = true
            showSignupStudentForm()
        }


      /*  submitBtn.setOnClickListener{
            checkEmailexits()
        }

        cancleBtn.setOnClickListener{
            finish()
        }*/

    }


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

    private fun showSignupStudentForm() {
        val paramsLogin = llSignin2.getLayoutParams() as PercentRelativeLayout.LayoutParams
        val infoLogin = paramsLogin.percentLayoutInfo
        infoLogin.widthPercent = 0.85f
        llSignin2.requestLayout()


        val paramsSignup = llSignup2.getLayoutParams() as PercentRelativeLayout.LayoutParams
        val infoSignup = paramsSignup.percentLayoutInfo
        infoSignup.widthPercent = 0.15f
        llSignup2.requestLayout()

        val translate = AnimationUtils.loadAnimation(applicationContext, R.anim.translate_left_to_right)
        llSignin2.startAnimation(translate)

        tvSignupInvoker2.setVisibility(View.VISIBLE)
        tvSigninInvoker2.setVisibility(View.GONE)
    }



    /*private fun checkEmailexits(){

        var email = emailRegisText.text.toString()
        var password = pwdText.text.toString()
        var name = nameText.text.toString()
        var lastname = lastnameText.text.toString()
        var status = statusText.text.toString()
        var phone = phoneText.text.toString()
        var school = schoolRegisText.text.toString()
        val user = FirebaseAuth.getInstance().currentUser

        if(!email.isEmpty() && !password.isEmpty() && !name.isEmpty() && !lastname.isEmpty() && !status.isEmpty() && !phone.isEmpty()
                && password.length >= 6  && !school.isEmpty()) {
            mAuth!!.fetchProvidersForEmail(emailRegisText.text.toString())
                    .addOnCompleteListener(){
                        var check = !it.getResult().providers!!.isEmpty()
                        if(!check){
                            mAuth!!.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener{
                                        //val messageId = database.push().key
                                        val messageData = Member(user!!.uid,email, password, name,lastname,status,phone,school,"","","","")
                                        database.child(user!!.uid).setValue(messageData).addOnCompleteListener({
                                            Toast.makeText(applicationContext,"Completely",Toast.LENGTH_SHORT).show()
                                        })

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
    }*/

}