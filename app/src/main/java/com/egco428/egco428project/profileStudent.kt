package com.egco428.egco428project

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_profile_student.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase







class profileStudent : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_student)

        historyBtn.setOnClickListener(this)

        // Write a message to the database
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")

        myRef.setValue("Hello, World!")

    }

    override fun onClick(v: View?) {
        if(v===historyBtn){
        }
    }

}
