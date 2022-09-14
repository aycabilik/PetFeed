package com.example.petfeed

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.petfeed.fragments.LiveFragment
import com.example.petfeed.fragments.MapsFragment
import com.example.petfeed.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {



    private val liveFragment = LiveFragment()
    private val profileFragment = ProfileFragment()
    private val mapsFragment = MapsFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        replaceFragment(liveFragment)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener  {
            when(it.itemId){
                R.id.liveChatIcon -> replaceFragment(liveFragment)
                R.id.mapIcon -> replaceFragment(mapsFragment)
                R.id.profileIcon -> replaceFragment(profileFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        if(fragment != null){
            val transaction =  supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container,fragment)
            transaction.commit()
        }

    }


}