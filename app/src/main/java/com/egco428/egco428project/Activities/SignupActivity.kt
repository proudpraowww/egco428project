package com.egco428.egco428project.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_signup.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import android.R.attr.password
import android.os.Message
import android.util.Log
import com.egco428.egco428project.Model.Member
import com.egco428.egco428project.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignupActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Members")

        submitBtn.setOnClickListener{
            register()
            //finish()
        }

        cancleBtn.setOnClickListener{
            finish()
        }
    }

    private fun register(){

        var email = emailText.text.toString()
        var password = pwdText.text.toString()
        var name = nameText.text.toString()
        var lastname = lastnameText.text.toString()
        var status = statusText.text.toString()
        var phone = phoneText.text.toString()

        if(!email.isEmpty() && !password.isEmpty() && !name.isEmpty() && !lastname.isEmpty() && !status.isEmpty() && !phone.isEmpty() && password.length >= 6){
            mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener{
                    val messageId = database.push().key
                    val messageData = Member(messageId!!,email, password, name,lastname,status,phone)
                    database.child(messageId).setValue(messageData).addOnCompleteListener({
                        Toast.makeText(applicationContext,"Completely",Toast.LENGTH_SHORT).show()
                    })

                    val intent = Intent(this, SigninActivity::class.java)
                    startActivity(intent)

                }
                .addOnFailureListener{
                    Toast.makeText(applicationContext,"Failed....",Toast.LENGTH_SHORT).show()
                    Log.d("Signup Failed",it.printStackTrace().toString())
                }

        } else{
            Toast.makeText(this,"Please enter", Toast.LENGTH_SHORT).show()
        }
    }

}