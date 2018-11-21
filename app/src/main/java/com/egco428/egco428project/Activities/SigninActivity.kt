package com.egco428.egco428project.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import android.content.Intent
import com.egco428.egco428project.R
import kotlinx.android.synthetic.main.activity_signin.*

class SigninActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mAuthTest: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        mAuth = FirebaseAuth.getInstance()

        /*val user = mAuth!!.currentUser
        if(user != null){
            val intent = Intent(this,profileStudent::class.java)
            startActivity(intent)
            finish()
        }*/

        signinBtn.setOnClickListener{

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

        signupBtn.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        forgotBtn.setOnClickListener{
            val intent = Intent(this, ForgotpasswordActivity::class.java)
            startActivity(intent)
        }

    }

    private fun login(email: String, password: String){
        mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth!!.getCurrentUser()
                        Toast.makeText(applicationContext,"Sigin Success Fully....",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
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
