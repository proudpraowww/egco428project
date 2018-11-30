package com.egco428.egco428project.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import android.content.Intent
import android.support.percent.PercentRelativeLayout
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import com.egco428.egco428project.Model.Member
import com.egco428.egco428project.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.activity_type_signin.*
import kotlinx.android.synthetic.main.activity_type_signup.*

class SigninActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mAuthTest: FirebaseAuth? = null
    lateinit var database: DatabaseReference
    private var status: String? = null
    private var currentEmail: String? = null
    private var memberList: ArrayList<Member>? = ArrayList()
    private var isSigninScreen: Boolean? = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Members")
        showSigninForm()

        tvSignupInvoker.setOnClickListener {
            isSigninScreen = false
            showSignupForm()
        }

        tvSigninInvoker.setOnClickListener {
            isSigninScreen = true
            showSigninForm()
        }

        Btnstudent.setOnClickListener{
            val intent = Intent(this,SignupActivity::class.java)
            startActivity(intent)
        }

        Btnteacher.setOnClickListener{
            val intent = Intent(this,SignupActivity::class.java)
            startActivity(intent)
        }


        /*val user = mAuth!!.currentUser
        if(user != null){
            val intent = Intent(this,profileStudent::class.java)
            startActivity(intent)
            finish()
        }*/

        btnSignin.setOnClickListener{

            var email = usernameText.text.toString()
            var password = passwordText.text.toString()

            if(email.isEmpty()){
                usernameText.error = "Please enter your name"
            }
            if(password.isEmpty()){
                passwordText.error = "Please enter your password"
            }
            if(!email.isEmpty() && !password.isEmpty()){
                login(email,password)
            }
        }

        /*signupBtn.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        forgotBtn.setOnClickListener{
            val intent = Intent(this, ForgotpasswordActivity::class.java)
            startActivity(intent)
        }*/

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //val value = dataSnapshot.getValue(Member::class.java)
                //val user = mAuth!!.getCurrentUser()
                val children = dataSnapshot!!.children
                children.forEach{
                    //var member: Member
                    //member = Member(it.child("id").value.toString(), it.child("email").value.toString(), it.child("password").value.toString(), it.child("name").value.toString(), it.child("lastname").value.toString(), it.child("status").value.toString(), it.child("phone").value.toString())
                    if(currentEmail.equals( it.child("email").value.toString())){
                        //currentEmail
                        Log.d("Status", it.child("status").value.toString())
                        if(it.child("status").value.toString().equals("student")){
                            //user_profile_name.setText(it.child("name").value.toString())
                            status = "student"
                            Log.d("Name", it.child("name").value.toString())
                        } else if(it.child("status").value.toString().equals("tutor")){
                            status = "tutor"
                        } else {
                            status = ""
                        }
                    }


                    var member: Member
                    member = Member(
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

                    memberList!!.add(member)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Toast.makeText(applicationContext, "data read failed.", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun showSignupForm() {
        val paramsLogin = llSignin.getLayoutParams() as PercentRelativeLayout.LayoutParams
        val infoLogin = paramsLogin.percentLayoutInfo
        infoLogin.widthPercent = 0.15f
        llSignin.requestLayout()


        val paramsSignup = llSignup.getLayoutParams() as PercentRelativeLayout.LayoutParams
        val infoSignup = paramsSignup.percentLayoutInfo
        infoSignup.widthPercent = 0.85f
        llSignup.requestLayout()

        tvSignupInvoker.setVisibility(View.GONE)
        tvSigninInvoker.setVisibility(View.VISIBLE)
        val translate = AnimationUtils.loadAnimation(applicationContext, R.anim.translate_right_to_left)
        llSignup.startAnimation(translate)

    }

    private fun showSigninForm() {
        val paramsLogin = llSignin.getLayoutParams() as PercentRelativeLayout.LayoutParams
        val infoLogin = paramsLogin.percentLayoutInfo
        infoLogin.widthPercent = 0.85f
        llSignin.requestLayout()


        val paramsSignup = llSignup.getLayoutParams() as PercentRelativeLayout.LayoutParams
        val infoSignup = paramsSignup.percentLayoutInfo
        infoSignup.widthPercent = 0.15f
        llSignup.requestLayout()

        val translate = AnimationUtils.loadAnimation(applicationContext, R.anim.translate_left_to_right)
        llSignin.startAnimation(translate)

        tvSignupInvoker.setVisibility(View.VISIBLE)
        tvSigninInvoker.setVisibility(View.GONE)
    }

    private fun login(email: String, password: String) {
        mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth!!.getCurrentUser()
                        //Toast.makeText(applicationContext,"Signin Successfully...",Toast.LENGTH_SHORT).show()
                        //Log.d("status variable", status)
                        currentEmail = user!!.email

                        for (member in memberList!!) {
                            if (member.email.equals(currentEmail)) {
                                Toast.makeText(applicationContext, "Signin Successfully...", Toast.LENGTH_SHORT).show()
                                Log.d("Signin1","Continue")
                                if (member.status.equals("student")) {
                                    val intent = Intent(this@SigninActivity, MainActivity::class.java)
                                    startActivity(intent)
                                } else if (member.status.equals("tutor")) {
                                    val intent = Intent(this@SigninActivity, TutorActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(applicationContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.getCurrentUser()
    }


}
