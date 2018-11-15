package com.egco428.egco428project.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import com.egco428.egco428project.Fragments.MapFragment
import com.egco428.egco428project.Fragments.ProfileFragment
import com.egco428.egco428project.Fragments.RequestFragment
import com.egco428.egco428project.R
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

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
