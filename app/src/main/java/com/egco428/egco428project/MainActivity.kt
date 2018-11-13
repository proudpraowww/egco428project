package com.egco428.egco428project

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var x:Int = 0
        var y:Int = 0

        button.setOnClickListener{
            val intent = Intent(this,profileStudent::class.java)
            startActivity(intent)
        }

    }
}
