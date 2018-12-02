package com.egco428.egco428project.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.egco428.egco428project.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgotpassword.*

class ForgotpasswordActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpassword)

        mAuth = FirebaseAuth.getInstance()

        //Click to reset password then, message will send into the your email
        Btnresetpassword.setOnClickListener{
            var email = resetText.text.toString()

            if (resetText.text.toString().isEmpty()) {
                Toast.makeText(applicationContext, "Enter your email!", Toast.LENGTH_SHORT).show()
            } else{
                mAuth!!.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Check email to reset your password!", Toast.LENGTH_SHORT).show()

                            } else {
                                Toast.makeText(this, "Fail to send reset password email!", Toast.LENGTH_SHORT).show()

                            }
                        }
            }
        }
    }
}
