package com.egco428.egco428project.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.widget.Toast
import com.egco428.egco428project.Fragments.MapFragment
import com.egco428.egco428project.Fragments.ProfileFragment
import com.egco428.egco428project.Fragments.RequestFragment
import com.egco428.egco428project.Model.Member
import com.egco428.egco428project.R
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*

class MainActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var database: DatabaseReference
    lateinit var currentEmail: String

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.profileNavigation -> {
                pushFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.mapNavigation -> {
                pushFragment(MapFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.requestNavigation -> {
                pushFragment(RequestFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        pushFragment(ProfileFragment())
        mapNavigation.setOnClickListener({
            bottomView.selectedItemId = R.id.mapNavigation
        })

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("Members")

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // Name, email address, and profile photo Url
            val name = user.displayName
            currentEmail = user.email.toString()
            val photoUrl = user.photoUrl
            val emailVerified = user.isEmailVerified
            val uid = user.uid
        }

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //val value = dataSnapshot.getValue(Member::class.java)
                val children = dataSnapshot!!.children
                children.forEach{
                     var member: Member
                     //member = Member(it.child("id").value.toString(), it.child("email").value.toString(), it.child("password").value.toString(), it.child("name").value.toString(), it.child("lastname").value.toString(), it.child("status").value.toString(), it.child("phone").value.toString())
                    if(it.child("email").value.toString().equals(currentEmail)){
                        //a4.setText(it.child("name").value.toString())
                        user_profile_name.setText(it.child("name").value.toString())
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Toast.makeText(applicationContext, "data read failed.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun pushFragment(fragment: Fragment?) {
        if (fragment == null)
            return
        val fragmentManager = supportFragmentManager //getFragmentManager();
        if (fragmentManager != null) {
            val ft = fragmentManager.beginTransaction()
            if (ft != null) {
                ft.replace(R.id.mainLayout, fragment)
                ft.commit()
            }
        }
    }

    /*private fun logout(){
        mAuth!!.signOut()
        val intent = Intent(this,SignActivity::class.java)
        startActivity(intent)
    }*/
}
