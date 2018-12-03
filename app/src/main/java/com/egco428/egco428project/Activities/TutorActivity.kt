package com.egco428.egco428project.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import com.egco428.egco428project.Fragments.*
import com.egco428.egco428project.R
import kotlinx.android.synthetic.main.activity_tutor.*

class TutorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutor)
        bottomView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        pushFragment(ProfileTutorFragment())
        mapNavigation.setOnClickListener({
            bottomView.selectedItemId = R.id.mapNavigation
            mapNavigation.setImageResource(R.drawable.worldwidebold)
        })
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.profileNavigation -> {
                pushFragment(ProfileTutorFragment())
                mapNavigation.setImageResource(R.drawable.worldwide)
                return@OnNavigationItemSelectedListener true
            }
            R.id.mapNavigation -> {
                pushFragment(MapTutorFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.requestNavigation -> {
                pushFragment(RequestTutorFragment())
                mapNavigation.setImageResource(R.drawable.worldwide)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
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
}
